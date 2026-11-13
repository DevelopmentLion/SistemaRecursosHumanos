package rhumanos

import grails.converters.JSON
import groovy.time.TimeCategory
import java.util.Calendar
import groovy.time.TimeDuration

import groovy.sql.Sql
import java.sql.*

import com.rhumanos.RhService

import com.rhumanos.RhAreaLaboral
import com.rhumanos.RhCodigosPersonal
import com.rhumanos.RhFuncionReal
import com.rhumanos.RhJornada
import com.rhumanos.RhRelacion
import com.rhumanos.RhTipoIncidencia

import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.SpringSecurityUtils





class RhumanosController {
    def springSecurityService
    def rhService
    def dataSource
    
    def index() {
        //
    }
    def checkLogin(){
        params.username = springSecurityService.principal.username
        //params.roles = springSecurityService.getPrincipal().getAuthorities()
        params.roles = SpringSecurityUtils.getPrincipalAuthorities()
        render (params as JSON)
    }
    def listTrabajadores(){
        
        def t = ''
        def datos = ''
        def total = ''
        //params.id = params.listaEmpleado.isNumber()
        
        if (params.listaEmpleado && params.listaEmpleado.isNumber()){
            datos = rhumanos.RhTrabajadores.findAllById(params.listaEmpleado,[max: params.limit, offset: params.start,sort:"id", order:"desc"])
            total = 1
        } else {
            t = rhumanos.RhTrabajadores.createCriteria()
            
            datos = t.list([max: params.limit, offset: params.start,sort:"id", order:"desc"]){

                if (params.listaEmpleado){
                    or {
                        like('nombres', '%' + params.listaEmpleado + '%')
                        like('apellidoPaterno', '%' + params.listaEmpleado + '%')
                        like('apellidoMaterno', '%' + params.listaEmpleado + '%')
                    }    
                }
                if (params.listaJornada){
                    eq ('turno',com.rhumanos.RhJornada.get(params.listaJornada))
                }
            }
            total = datos.totalCount
        }
        def root = datos.collect{
            return [
                id:it.id,
                nombres:it.nombres,
                apellidoPaterno:it.apellidoPaterno,
                apellidoMaterno:it.apellidoMaterno,

                activo:it.activo,
                
                rfc:it.rfc,
                curp:it.curp,
                telefonos:it.telefonos,
                email:it.email,
                
                codigoTrabajador:it.codigoTrabajador.id,
                codigoTrabajadorCode:it.codigoTrabajador.codigo,
                codigoTrabajadorDesc:it.codigoTrabajador.descripcion,
                
                relacionLaboral:it.relacionLaboral.id,
                relacionLaboralDesc:it.relacionLaboral.relacion,
                
                areaLaboral:it.areaLaboral.id,
                areaLaboralDesc:it.areaLaboral.areaLaboral,
                
                funcionReal:it.funcionReal.id,
                funcionRealDesc:it.funcionReal.funcionReal,
                
                turno:it.turno.id,
                turnoDesc:it.turno.jornada,
                
                clavePago:it.clavePago,
                horarioTexto:it.horarioTexto,
                
                observacionesReporte:it.observacionesReporte,
                observaciones:it.observaciones

            ]
        }
        
        render ([root:root,total:total] as JSON)
    }
    
def archivoRegistros(){
        // En Grails 6 (Spring Boot) las peticiones multipart llegan envueltas en
        // StandardMultipartHttpServletRequest; se usa la API de Spring para
        // obtener el archivo de forma fiable.
        def archivo = (request instanceof org.springframework.web.multipart.MultipartHttpServletRequest) ?
                request.getFile('archivoRegistros') : null

        if (!archivo) {
            render ([success:false, estado:false, mensaje:'No se recibió el archivo "archivoRegistros"'] as JSON)
            return
        }

        def datosArchivo = [
            type:archivo.contentType,
            originalName:archivo.originalFilename,
            size:archivo.size
        ]
        
        // En Grails 6 (Spring Boot) servletContext.getRealPath() devuelve null,
        // por lo que el directorio se toma de la configuración rh.payloadDir
        // (relativo al directorio de trabajo de la aplicación).
        def payloadDir = grailsApplication.config.getProperty('rh.payloadDir', 'payload')
        def userDir = new File(payloadDir)
        userDir.mkdirs()
        
        archivo.transferTo( new File( userDir, archivo.originalFilename))
        
        def contenidoArchivo = new File( userDir, archivo.originalFilename )
        
        contenidoArchivo = contenidoArchivo.text
        
        
        
        // Cuenta las líneas del archivo
        def totalLineas = contenidoArchivo.readLines()

        def fecha = ''
        def lineaEntradaIndex = 0
        def valido = true
        def mensaje = ''
        def mensajeCompleto=[]
        def entradasProcesadas = 0
        def fechaArchivo = ''
        def fechaInicial = ''
        def fechaFinal = ''
        def archivoFecha = ''
        def entradasErroneas=0
        def entradasDuplicadas =0
        
        def fechaInicialQuery
        def fechaFinalQuery
        
        def datosConsultaTotal
        def tipoInci = com.rhumanos.RhTipoIncidencia.get(100)
        
        // Verifica que el archivo sea válido y tenga el fomato correcto
        try {
            
            totalLineas.each{line,lineNo = 1 ->
                def valoresLinea = line.tokenize( ',' )
                // Todos los archivos de este tipo inician con el caracter '1'.
                // Esto marca el inicio de las líneas de archivo
                if (valoresLinea[0] == '1'){
                    // Incrementa el contador para que pueda continuar
                    lineNo++
                } else {
                    // Si el segundo grupo de caracteres de esta linea inicia con '4'
                    // Inicia una sección de fechas de registo
                    if (valoresLinea[1] == '4'){
                        if (lineNo == 1){
                            fechaInicialQuery = new Date().parse("yyyyMMdd HH:mm:ss", valoresLinea[2]+' 00:00:00')
                        }
                        fechaFinalQuery = new Date().parse("yyyyMMdd HH:mm:ss", valoresLinea[2]+' 23:59:59')
                        // Incrementa el contador para que pueda continuar
                        lineNo++
                    } else {
                        // Si el valor ddel segundo registro es igual a '3', es un registro de asistencia
                        if (valoresLinea[1] == '3'){
                            // Incrementa el contador para que pueda continuar
                            lineNo++
                        } else  {
                            // Sale del ciclo con un mensaje de error
                            throw new Exception("El archivo no parece tener el formato adecuado.") 
                        }
                    }
                }
            }
        } catch (Exception e) {
            valido = false
            mensaje = e.message
        }
        //println fechaInicialQuery
        //println fechaFinalQuery

        datosConsultaTotal = rhumanos.RegistroAsistencia.findAllByFechaAmparadaBetweenAndTipoIncidencia(fechaInicialQuery, fechaFinalQuery, tipoInci)
        //println datosConsultaTotal
        // Si es válido, empieza a procesar esta linea.
        if (valido == true) {
            totalLineas.each{line,lineNo=0 ->
                lineNo++
                def valoresLinea = line.tokenize( ',' )
                if (valoresLinea[0] == '1'){
                    //println 'Registro de la fecha de creación del archivo es: '+valoresLinea[1]
                    archivoFecha = valoresLinea[1]+'-'+archivo.originalFilename
                    lineNo++
                } else {
                    if (valoresLinea[1] == '4'){
                        //println 'La fecha de este registro es: '+valoresLinea[2]
                        fecha = valoresLinea[2]
                        lineNo++
                    } else {
                        if (valoresLinea[1] == '3'){
                            def registroFecha = new Date().parse("yyyyMMdd HHmmss", fecha+' '+valoresLinea[0])
                            
                            def nuevoRegistro = [:]

                            // Para mostrar en la salida el rango de entrada del archivo de registros (fecha larga)
                            if (lineaEntradaIndex < 1) {
                                fechaInicial = new java.text.SimpleDateFormat("EEEE MMMM dd 'de' yyyy HH:mm").format(registroFecha)
                                
                            }
                            
                            fechaFinal = new java.text.SimpleDateFormat("EEEE MMMM dd 'de' yyyy HH:mm").format(registroFecha)
                            
                            
                            
                            
                            // Preparando los datos para el nuevo registro
                            
                            // -------------------------------------------
                            // Cambiar todo esto a SQL
                            // -------------------------------------------
                            
                            
                            
                            // Consulta para ver si el registro ya existe (obtiene un numero)
                            def trabajador = rhumanos.RhTrabajadores.get(valoresLinea[2].toInteger())
                            //def rq = rhumanos.RegistroAsistencia.countByTrabajadorAndFechaAmparada(trabajador, registroFecha)
                            //def consulta = 
                            def rq = false
                            def conteo = datosConsultaTotal.find{ p -> p.fechaAmparada == registroFecha && p.tipoIncidencia == tipoInci && trabajador==trabajador
                                rq = true
                            }
                            
                            /*
                            def trab = valoresLinea[2].toInteger()
                            def rq = sql.firstRow("SELECT id FROM registro_asistencia WHERE registro_asistencia.fecha_amparada=? AND registro_asistencia.trabajador_id=? AND registro_asistencia.tipo_incidencia_id = 100",
                                [
                                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(registroFecha),
                                    trab
                                ]
                            )
                            println rq
                            
                            println 'Contando: '+rq+' de '+trab+' en '+registroFecha +'('+new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(registroFecha)+')'
                            */
                           //Si el número es cero, crea el nuevo registro
                            if (!rq){
                                //println 'Insertando...' 
                                nuevoRegistro.trabajador = trabajador
                                nuevoRegistro.fechaAmparada = registroFecha
                                nuevoRegistro.tipoIncidencia = com.rhumanos.RhTipoIncidencia.get(100)

                                nuevoRegistro.creado = springSecurityService.principal.username

                                nuevoRegistro.tipoRegistro = 1
                                nuevoRegistro.archivo = archivo.originalFilename
                                nuevoRegistro.fechaArchivo = registroFecha

                                nuevoRegistro.lineaArchivo = lineNo
                            
                                def i = new rhumanos.RegistroAsistencia(nuevoRegistro)
                                //sql = new Sql(dataSource)
                                //sql.
                                if (i.save(flush:true)){
                                    //println 
                                    entradasProcesadas++
                                    lineNo++
                                    lineaEntradaIndex++
                                } else {
                                    entradasErroneas++
                                    //println i.errors.allErrors
                                }
                            } else {
                                entradasDuplicadas++  //Si el número es mayor o igual que uno, sumale un número a la variable que avisa que no se pudo agregar
                            }
                            
                            
                            
//                            entradasProcesadas++
//                            lineNo++
//                            lineaEntradaIndex++
                        } else  {
                            valido=false
                            mensaje= 'El archivo no parece tener el formato adecuado.' 
                            println 'El archivo no parece tener el formato adecuado.' 
                        }
                    }
                }
            }
            mensaje = 'Archivo: '+archivoFecha+'<br/>'
            mensaje= mensaje + 'Entrdas procesadas: '+entradasProcesadas+'<br/>'
            mensaje = mensaje+'Primer registro: '+fechaInicial+'<br/>'
            mensaje = mensaje+'Último registro: '+fechaFinal+'<br/>'
            mensaje = mensaje+'Entradas con errores: '+entradasErroneas+'<br/>'
            mensaje = mensaje+'Entradas duplicadas: '+entradasDuplicadas
            
            mensajeCompleto=[
                //mensaje:mensaje,
                archivo:archivoFecha,
                entradas: entradasProcesadas,
                inicio: fechaInicial,
                fin: fechaFinal,
                errores: entradasErroneas,
                duplicadas: entradasDuplicadas
            ]
        } else {
            valido = false
            mensaje= 'El archivo no parece tener el formato adecuado.' 
        }
        render ([success:true,estado:valido,mensaje:mensaje, mensajeCompleto:mensajeCompleto] as JSON)
    }
    
    def guardaTrabajador(){
        def trabajador = ''
        
        if (params.codigoTrabajador){
            params.codigoTrabajador = com.rhumanos.RhCodigosPersonal.get(params.codigoTrabajador)
        }
        if (params.relacionLaboral){
            params.relacionLaboral = com.rhumanos.RhRelacion.get(params.relacionLaboral)
        }
        if (params.areaLaboral){
            params.areaLaboral = com.rhumanos.RhAreaLaboral.get(params.areaLaboral)
        }
        if (params.funcionReal){
            params.funcionReal = com.rhumanos.RhFuncionReal.get(params.funcionReal)
        }
        if (params.turno){
            params.turno = com.rhumanos.RhJornada.get(params.turno)
        }
        
        if (params.id){
            trabajador = rhumanos.RhTrabajadores.get(params.id)
            trabajador.properties = params
        } else {
            trabajador = new rhumanos.RhTrabajadores(params)
        }
        
        if (trabajador.save(flush:true)) {
            def root = trabajador.collect{
                return [
                    id:it.id,
                    nombres:it.nombres,
                    apellidoPaterno:it.apellidoPaterno,
                    apellidoMaterno:it.apellidoMaterno,
                    activo:it.activo,

                    rfc:it.rfc,
                    curp:it.curp,
                    telefonos:it.telefonos,
                    email:it.email,

                    codigoTrabajador:it.codigoTrabajador.id,
                    codigoTrabajadorCode:it.codigoTrabajador.codigo,
                    codigoTrabajadorDesc:it.codigoTrabajador.descripcion,

                    relacionLaboral:it.relacionLaboral.id,
                    relacionLaboralDesc:it.relacionLaboral.relacion,

                    areaLaboral:it.areaLaboral.id,
                    areaLaboralDesc:it.areaLaboral.areaLaboral,

                    funcionReal:it.funcionReal.id,
                    funcionRealDesc:it.funcionReal.funcionReal,

                    turno:it.turno.id,
                    turnoDesc:it.turno.jornada,

                    clavePago:it.clavePago,
                    horarioTexto:it.horarioTexto,

                    observacionesReporte:it.observacionesReporte,
                    observaciones:it.observaciones

                ]
            }
            
            render ([success:'true',estado:'true', root:root] as JSON)
        } else {
            render ([success:'true',estado:'false',root:trabajador.errors.allErrors] as JSON)
        }
    }
    
    /* *************************************************************
     * Grarda (y actualiza) trabajador
     * 
     * 
     * @author          mmateos
     * @since           2020-07-09
     * @version         1.0
     * 
     * @param           trabajador                          El id del trabajador
     * 
     ****************************************************************** */
    def guardaTrabajadorV2(){
        
        def dat = request.JSON
        
        def trabajador = ''
        
        if (dat.codigoTrabajador){
            dat.codigoTrabajador = com.rhumanos.RhCodigosPersonal.get(dat.codigoTrabajador)
        }
        if (dat.relacionLaboral){
            dat.relacionLaboral = com.rhumanos.RhRelacion.get(dat.relacionLaboral)
        }
        if (dat.areaLaboral){
            dat.areaLaboral = com.rhumanos.RhAreaLaboral.get(dat.areaLaboral)
        }
        if (dat.funcionReal){
            dat.funcionReal = com.rhumanos.RhFuncionReal.get(dat.funcionReal)
        }
        if (dat.turno){
            dat.turno = com.rhumanos.RhJornada.get(dat.turno)
        }
        
        // Para modificar activo
        
        
        if (dat.id){
            trabajador = rhumanos.RhTrabajadores.get(dat.id)
            trabajador.properties = dat
        } else {
            trabajador = new rhumanos.RhTrabajadores(dat)
        }
        
        if (trabajador.save(flush:true)) {
            def root = trabajador.collect{
                return [
                    id:it.id,
                    nombres:it.nombres,
                    apellidoPaterno:it.apellidoPaterno,
                    apellidoMaterno:it.apellidoMaterno,
                    activo:it.activo,

                    rfc:it.rfc,
                    curp:it.curp,
                    telefonos:it.telefonos,
                    email:it.email,

                    codigoTrabajador:it.codigoTrabajador.id,
                    codigoTrabajadorCode:it.codigoTrabajador.codigo,
                    codigoTrabajadorDesc:it.codigoTrabajador.descripcion,

                    relacionLaboral:it.relacionLaboral.id,
                    relacionLaboralDesc:it.relacionLaboral.relacion,

                    areaLaboral:it.areaLaboral.id,
                    areaLaboralDesc:it.areaLaboral.areaLaboral,

                    funcionReal:it.funcionReal.id,
                    funcionRealDesc:it.funcionReal.funcionReal,

                    turno:it.turno.id,
                    turnoDesc:it.turno.jornada,

                    clavePago:it.clavePago,
                    horarioTexto:it.horarioTexto,

                    observacionesReporte:it.observacionesReporte,
                    observaciones:it.observaciones

                ]
            }[0]
            
            render ([success:true, estado:true, root:root] as JSON)
        } else {
            render ([success:true, estado:false,root:trabajador.errors.allErrors] as JSON)
        }
    }
    
    
    @Secured(['ROLE_RH_USUARIO', 'ROLE_RH_ADMIN'])
    def guardaIncidencia(){
        def estado = ''
        def root = ''
        params.trabajador = rhumanos.RhTrabajadores.get(params.trabajador)
        params.tipoIncidencia = com.rhumanos.RhTipoIncidencia.get(params.tipoIncidencia)
        params.fechaAmparada = Date.parse("dd/MM/yyyy", params.fechaAmparada)
        params.creado = springSecurityService.principal.username
        
        
        
        def numeroIncidencias = rhumanos.RegistroAsistencia.countByTrabajadorAndTipoRegistroAndFechaAmparada(params.trabajador,2,params.fechaAmparada)
        
        if (numeroIncidencias > 0) {
            estado='repetido'
            root=[repetidos:numeroIncidencias]
        } else {
        
            def i = new rhumanos.RegistroAsistencia(params)
            if (i.save(flush:true)){
                estado='true'
                root=i
            } else {
                estado='false'
                root=i.errors.allErrors
            }
        }
        render ([success:'true',estado:estado,root:root] as JSON)
        //render ([params:params] as JSON)
    }
    
    /* *************************************************************
     * Grarda nueva incidencia
     * 
     * 
     * @author          mmateos
     * @since           2020-07-05
     * @version         1.0
     * 
     * @param           trabajador                          El id del trabajador
     * @param           tipoIncidencia                      El id del tipo de incidencia
     * @param           fechaAmparada                       La fecha amaprada de este evento (en formato dd/mm/aaaa)
     * @param           observaciones                       Opcional, descripción de la incidencia
     * 
     ****************************************************************** */
    @Secured(['ROLE_RH_USUARIO', 'ROLE_RH_ADMIN'])
    def guardaIncidenciaV2(){
        def estado = false
        def root
        def dat = request.JSON
        
        dat.trabajador = rhumanos.RhTrabajadores.get(dat.trabajador)
        dat.tipoIncidencia = com.rhumanos.RhTipoIncidencia.get(dat.tipoIncidencia)
        dat.fechaAmparada = Date.parse("dd/MM/yyyy", dat.fechaAmparada)
        dat.creado = springSecurityService.principal.username
        
        
        
        def numeroIncidencias = rhumanos.RegistroAsistencia.countByTrabajadorAndTipoRegistroAndFechaAmparada(dat.trabajador,2,dat.fechaAmparada)

        if (numeroIncidencias > 0) {
            estado=false
            root=[repetidos:numeroIncidencias]
        } else {

            def i = new rhumanos.RegistroAsistencia(dat)
            if (i.save(flush:true)){
                estado=true
                root=[id:i]
            } else {
                estado=false
                root=i.errors.allErrors
            }
        }
        render ([success:true,estado:estado,root:root] as JSON)
        
    }
    /* *************************************************************
     * Grarda nueva incidencia
     *
     *
     * @author          mmateos
     * @since           2022-05-08
     * @version         1.0
     *
     * @param           trabajador                          El id del trabajador
     * @param           tipoIncidencia                      El id del tipo de incidencia
     * @param           fechaAmparada                       La fecha amaprada de este evento (ARRAY en formato aaaa-mm-dd)
     * @param           observaciones                       Opcional, descripción de la incidencia
     *
     ****************************************************************** */
    @Secured(['ROLE_RH_USUARIO', 'ROLE_RH_ADMIN'])
    def guardaIncidenciaV3(){
        def estado = false

        def root
        def dat = request.JSON
        def inci = [:]
        def mensaje = 'Fecha: '
        def dias
        def agregadas = 0

        def trabajador = rhumanos.RhTrabajadores.get(dat.trabajador)
        inci.tipoIncidencia = com.rhumanos.RhTipoIncidencia.get(dat.tipoIncidencia)
        inci.creado = springSecurityService.principal.username
        inci.tipoRegistro = 2
        inci.observaciones = dat.observaciones


        dat.fechaAmparada.each{fecha->
            inci.fechaAmparada = Date.parse("yyyy-MM-dd", fecha)
            def existe = rhumanos.RegistroAsistencia.countByTrabajadorAndTipoRegistroAndFechaAmparada(trabajador,2, inci.fechaAmparada)
            //println existe
            if (existe == 0){
                trabajador.addToIncidencias(new rhumanos.RegistroAsistencia(inci))
                agregadas = agregadas + 1
            } else {
                mensaje = mensaje + new java.text.SimpleDateFormat("dd-MM-yyyy").format(inci.fechaAmparada)+', '
            }
        }
        if (trabajador.save(flush:true)){
            estado = true
        } else {
            root=trabajador.errors.allErrors
        }

        render ([success:true,estado:estado, mensaje:mensaje, agregadas: agregadas, root:root] as JSON)

    }
    
    @Secured(['ROLE_RH_USUARIO', 'ROLE_RH_ADMIN'])
    def enviaIncidenciasEmail(){

        //def trabajador = rhumanos.RhTrabajadores.get(params.trabajador)
         
        def root = ''
        /*
        if (params.mesFilter) {
            root = rhService.serviceReporteAsistencia(params.mesFilter,params.yearFilter,params.trabajador)
        } else {
            root = rhService.serviceReporteAsistencia(params.page,params.yearFilter,params.trabajador)
        }
        */
        sendMail {
                async true
                from "sistemashno@hno.oaxaca.gob.mx"
                to params.email
                
                subject "Reportes de incidencias"
                //html "Se ha recibido un nueva solicitud de servicio con el número de folio "+datosPedido.folio+"<br/>recibido el "+datosPedido.dateCreated+". Favor de atender."
                html(view:"mail", model:[root:root])
            }
            
        render ([success:'true',estado:'true',root:'nada'] as JSON)
        /*
        params.rfc = trabajador.rfc
        params.nombre = trabajador.apellidoPaterno+' '+trabajador.apellidoMaterno+' '+trabajador.nombres
        params.noEmpleado = trabajador.id
        params.clavePago = trabajador.clavePago
        params.horarioAsignado = trabajador.horarioTexto
        params.observaciones = trabajador.observacionesReporte
        params._format = params.tipoFormato
        params._name = "rh_"+params.noEmpleado
        params._file = "reporteRhMes"
        
        
        chain(controller: "jasper", action: "index", model: [data:root], params:params)
        */
        //
    }
    
    
    @Secured(['ROLE_RH_USUARIO', 'ROLE_RH_ADMIN'])
    def editaIncidencia(){
        
        def parametros = [:]
        def estado = ''
        def root = ''
        def i = ''
        
        parametros.trabajador = rhumanos.RhTrabajadores.get(params.incidenciaEmpleado)
        parametros.tipoIncidencia = com.rhumanos.RhTipoIncidencia.get(params.incidenciaTipoIncidencia)
        parametros.fechaAmparada = Date.parse("dd/MM/yyyy", params.incidenciaFechaAmparada)
        parametros.tipoRegistro = 2
        parametros.creado = springSecurityService.principal.username
        
        parametros.observaciones = params.incidenciaObservaciones
        
        if (params.id){
            
            i = rhumanos.RegistroAsistencia.get(params.id)
            i.properties = params
            
        } else {
            i = new rhumanos.RegistroAsistencia(parametros)
        }
        
        if (i.save(flush:true)){
            estado='true'
            root=i
        } else {
            estado='false'
            root=i.errors.allErrors
        }
        
        render ([success:'true',estado:estado,root:root] as JSON)
        
    }
    /* *******************
     * Edita incidencia (con POST)
     * Para la nueva interface de recursos humanos
     * ******************** */
    @Secured(['ROLE_RH_USUARIO', 'ROLE_RH_ADMIN'])
    def editaIncidenciaV2(){
        def dat = request.JSON
        def parametros = [:]
        def estado = ''
        def root = ''
        def i = ''
        
        parametros.trabajador = rhumanos.RhTrabajadores.get(dat.incidenciaEmpleado)
        parametros.tipoIncidencia = com.rhumanos.RhTipoIncidencia.get(dat.incidenciaTipoIncidencia)
        parametros.fechaAmparada = Date.parse("dd/MM/yyyy", dat.incidenciaFechaAmparada)
        parametros.tipoRegistro = 2
        parametros.creado = springSecurityService.principal.username
        
        parametros.observaciones = dat.incidenciaObservaciones
        
        if (dat.id){
            
            i = rhumanos.RegistroAsistencia.get(dat.id)
            i.properties = parametros
            
        } else {
            i = new rhumanos.RegistroAsistencia(parametros)
        }
        
        if (i.save(flush:true)){
            estado=true
            root=i
        } else {
            estado=false
            root=i.errors.allErrors
        }
        
        render ([success:true,estado:estado,root:root] as JSON)
        
    }
    
    def getDatosIncidencia(){
        def i = rhumanos.RegistroAsistencia.get(params.id)
        def datos = i.collect{
            return[
                incidenciaId:it.id,
                incidenciaEmpleado:it.trabajador.id,
                incidenciaEmpleadoNombre:'('+it.trabajador.id+') '+it.trabajador.apellidoPaterno+' '+it.trabajador.apellidoMaterno+' '+it.trabajador.nombres,
                incidenciaTipoIncidencia:it.tipoIncidencia.id,
                incidenciaFechaAmparada:new java.text.SimpleDateFormat("dd/MM/yyyy").format(it.fechaAmparada),
                incidenciaObservaciones:it.observaciones
            ]
        }
        render (datos as JSON)
    }
    
    def listIncidencias(){
        def datos = ''
        
        def fechaDe
        def fechaA
        
        
        def i = rhumanos.RegistroAsistencia.createCriteria().list([max: params.limit, offset: params.start,sort:"id", order:"desc"]){
            if (params.trabajador){eq('trabajador',rhumanos.RhTrabajadores.get(params.trabajador))}
            if (params.tipoRegistro){eq('tipoRegistro',params.tipoRegistro)}
            if (params.tipoIncidencia){eq('tipoIncidencia', com.rhumanos.RhTipoIncidencia.get(params.tipoIncidencia as Long))}
            
            if (params.fechaDe){
                fechaDe = Date.parse("dd-MM-yyyy", params.fechaDe)
            
                if (params.fechaA){
                    use (TimeCategory) {
                        fechaA = Date.parse("dd-MM-yyyy", params.fechaA) + 1.day
                    }
                } else {
                    use (TimeCategory) {
                        fechaA = fechaDe + 1.day
                    }
                }
                between("fechaAmparada",fechaDe,fechaA)
            }
            /*
            if (params.fechaDe && params.fechaA){
                // OJO
                def fechaDe = new java.text.SimpleDateFormat("dd-MM-yyyy 00:00").format(params.fechaDe as String)
                def fechaA = new java.text.SimpleDateFormat("dd-MM-yyyy 23:59").format(params.fechaA as String)
                //-----
                between ('fechaAmparada',fechaDe, fechaA)
            }
            */
        }
        
        //def incidencias = i.list([max: params.limit, offset: params.start,sort:"id", order:"desc"])
        
        datos = i.collect{
            return [
                id:it.id,
                trabajador:it.trabajador.id,
                fechaAmparada:new java.text.SimpleDateFormat("dd-MM-yyyy").format(it.fechaAmparada),
                registroAsistencia:new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(it.fechaAmparada),
                tipoIncidencia:it.tipoIncidencia.id,
                observaciones:it.observaciones,
                dateCreated: new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(it.dateCreated),
                tipoRegistro:it.tipoRegistro,
                archivo:it.archivo,
                fechaArchivo: ((it.fechaArchivo) ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(it.fechaArchivo) : ' '),
                lineaArchivo:it.lineaArchivo,
                trabajadorNombre:it.trabajador.nombres+' '+it.trabajador.apellidoPaterno+' '+it.trabajador.apellidoMaterno,
                tipoIncidenciaDetalle:it.tipoIncidencia.tipoIncidencia
            ]
        }
        
        def total = i.totalCount
        
        render ([root:datos,total:total] as JSON)
        
    }

    
    def listRegistrosTrabajador(){
        def total = ''
        def root = ''
        
        if (!params.quincenaFilter&&params.mesFilter){
            total = 31
            root = rhService.serviceReporteAsistencia(params.mesFilter,params.yearFilter,params.trabajador)
        }
        // Calcula por año (da la pagina)
        if (!params.quincenaFilter&&!params.mesFilter&&params.yearFilter){
            total = 372
            root = rhService.serviceReporteAsistencia(params.page,params.yearFilter,params.trabajador)
        }
                
        render ([total:total,root:root] as JSON)
        
    }
    
    def printRegistro(){
        
        def trabajador = rhumanos.RhTrabajadores.get(params.trabajador as Long)
         
        def root = ''
        if (params.mesFilter) {
            root = rhService.serviceReporteAsistencia(params.mesFilter,params.yearFilter,params.trabajador)
        } else {
            root = rhService.serviceReporteAsistencia(params.page,params.yearFilter,params.trabajador)
        }

        
        params.rfc = trabajador.rfc
        params.nombre = trabajador.apellidoPaterno+' '+trabajador.apellidoMaterno+' '+trabajador.nombres
        params.noEmpleado = trabajador.id
        params.clavePago = trabajador.clavePago
        params.horarioAsignado = trabajador.horarioTexto
        params.observaciones = trabajador.observacionesReporte
        params._format = params.tipoFormato
        params._name = "rh_"+params.noEmpleado
        params._file = "reporteRhMes"
        
        //println root

        // NOTA: el plugin jasper no tiene versión compatible con Grails 6.
        // Mientras se integra un generador de reportes (DOCX/PDF), se devuelve
        // la información del reporte en JSON.
        render ([success:true, format:params._format, name:params._name, data:root] as JSON)
        //
    }

    def printMes2(){
        def mesFilter = params.mesFilter
        def yearFilter = params.yearFilter
        def jornada = params.jornada
        def tipo = params.tipo
        //def relacion = params.relacion
        def relacion = com.rhumanos.RhRelacion.get(params.relacion)
        
        def turno = com.rhumanos.RhJornada.get(params.jornada)
        
        def datos = []
        
        def fechaInicio = Date.parse("d-M-yyyy", 1+'-'+mesFilter+'-'+yearFilter)
        def fechaFin = ''
        // Sumale un mes para obtener el dia final
        use (TimeCategory) {
            fechaFin = fechaInicio + 1.month
        }
        

        // Busca a todas las personas del turno y relación laboral
        def pers = rhumanos.RhTrabajadores.findAllByTurnoAndRelacionLaboral(turno,relacion)
        // Busca alas personas del turno y relación laboral que tienen incidencias
        def incidencias = rhumanos.RegistroAsistencia.findAllByFechaAmparadaBetweenAndTrabajadorInList(fechaInicio, fechaFin ,pers).trabajador.id.unique()
        // Busca a las personas del turno y relación laboral que tienen registros de asistencia
        def checadas = rhumanos.RhRegistrosK1.findAllByDatetimeBetweenAndPersonInList(fechaInicio, fechaFin, pers.id).person.unique()
        // EL listado final...
        

        //trabajadores = trabajadores.unique()
        //def trabajadores = resultsT.id + resultsT2.person
        
        //Relaciones: 1:contrato,2:regularizado,3:formalizado,4:base,5:confianza
        //Turnos (o jornadas):1:matutino,2:vespertino,3:noccturno,4:especial 
        def trabajadores = incidencias + checadas
        trabajadores = trabajadores.unique().sort()
        //def trabajadores = resultsT
        trabajadores.each{
            datos = datos + rhService.serviceReporteAsistencia(mesFilter,yearFilter,it)
            //println it
        }
        
        //def datos = rhService.serviceReporteMensual(mesFilter,yearFilter)
        
        
        params._format = 'DOCX'
        params._name = "ReporteMensualRh-"+relacion.relacion+'-'+params.mesFilter+'-'+params.yearFilter
        params._file = "reporteRhMesTodos"


        // NOTA: el plugin jasper no tiene versión compatible con Grails 6.
        // Mientras se integra un generador de reportes (DOCX/PDF), se devuelve
        // la información del reporte en JSON.
        render ([success:true, format:params._format, name:params._name, data:datos] as JSON)


    }
    /**
     * Imprime el reporte mensual de recursos humanos
     * Nueva versión
     */
    def printMes(){
        def dat=[:]
        def fechaDe
        def fechaA

        // Manejo de fechaDe y fechaA
        def fd = "01-"+params.mesFilter+"-"+params.yearFilter
        dat.fechaDe=new java.text.SimpleDateFormat("dd-M-yyyy HH:mm:ss").parse(fd+' 00:00:00')
        use (TimeCategory) {
            dat.fechaA = dat.fechaDe + 1.month -1.minute
        }
        fechaDe = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dat.fechaDe)
        fechaA = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dat.fechaA)

        // Los paráteros para las consultas
        if(params.relacion){
            dat.relacion = com.rhumanos.RhRelacion.get(params.relacion)
        }
        if(params.jornada){
            dat.jornada = com.rhumanos.RhJornada.get(params.jornada)
        }

        // La consulta de incidencias. Genera un listado similar a la consulta SQL
        def incidencias = rhumanos.RegistroAsistencia.findAllByFechaAmparadaBetween(dat.fechaDe, dat.fechaA).collect {
            return [
                    total                   : 0,
                    nombre_completo         : it.trabajador.apellidoMaterno + ' ' + it.trabajador.apellidoPaterno + ' ' + it.trabajador.nombres,
                    curp                    : it.trabajador.curp,
                    id                      : it.trabajador.id,
                    rfc                     : it.trabajador.rfc,
                    apellido_paterno        : it.trabajador.apellidoPaterno,
                    apellido_materno        : it.trabajador.apellidoMaterno,
                    nombres                 : it.trabajador.nombres,
                    clave_pago              : it.trabajador.clavePago,
                    horario_texto           : it.trabajador.horarioTexto,
                    jornada                 : it.trabajador.turno.jornada,
                    jid                     : it.trabajador.turno.id,
                    observaciones_reporte   : it.trabajador.observacionesReporte,
                    observaciones           : it.trabajador.observaciones,
                    relacion                : it.trabajador.relacionLaboral.relacion,
                    rid                     : it.trabajador.relacionLaboral.id,
                    codigo                  : it.trabajador.codigoTrabajador.codigo,
                    descripcion             : it.trabajador.codigoTrabajador.descripcion,
                    area_laboral            : it.trabajador.areaLaboral.areaLaboral,
                    fecha_larga             : new java.text.SimpleDateFormat("EEEE MMMM dd 'de' yyyy").format(it.fechaAmparada),
                    horas                   : "",
                    incidencia              : it.tipoIncidencia.tipoIncidencia,
                    incidencia_observaciones: it.observaciones,
                    fecha                   : new java.text.SimpleDateFormat("dd-MM-yyyy").format(it.fechaAmparada),
                    dia                     : new java.text.SimpleDateFormat("dd").format(it.fechaAmparada),
                    mes                     : new java.text.SimpleDateFormat("MM").format(it.fechaAmparada),
                    anio                    : new java.text.SimpleDateFormat("yyyy").format(it.fechaAmparada)
            ]
        }

        //filtra incidencias por params.jornada == incidencias.jid
        incidencias = incidencias.findAll{it.jid == params.jornada as Long}

        // filtra incidencias por params.relacion == incidencias.rid
        incidencias = incidencias.findAll{it.rid == params.relacion as Long}

        // Saca el listado de los ID de los trabajadores
        def trabajadoresIncidencias = incidencias.collect{it.id}.unique()

        // Ejecuta la consulta SQL de registros de asistencia.
        // En Grails 6 (Spring Boot) getRealPath() devuelve null, por lo que la
        // consulta se lee como recurso del classpath (src/main/resources/sql).
        def sqlResource = getClass().classLoader.getResource('sql/ReporteRh.sql')
        if (!sqlResource) {
            render ([success:false, estado:false, mensaje:'No se encontró el recurso sql/ReporteRh.sql en el classpath'] as JSON)
            return
        }
        def consulta = sqlResource.text

        def sql = new Sql(dataSource)
        def rows = sql.rows(consulta, [fechaDe:fechaDe, fechaA:fechaA, jornada:params.jornada as Long, relacion:params.relacion as Long])
        sql.close()

        // actualiza el campo de fecha_larga en rows para formato de fecha larga
        rows.each{it.fecha_larga = new java.text.SimpleDateFormat("dd-MM-yyyy").parse(it.fecha).format("EEEE MMMM dd yyyy")}

        // Itera rows y busca equivalencia en incidencias comparando por id y por fecha.
        // SI existe una incidencia, toma los campos incidencia e incidencia_observaciones de la coincidencia y los agrega a los mismos campos en el row
        rows.each{row->
            incidencias.each{incidencia->
                if (row.id == incidencia.id && row.dia == incidencia.dia){
                    row.incidencia = incidencia.incidencia
                    row.incidencia_observaciones = incidencia.incidencia_observaciones
                }
            }
        }

        // Itera incidencia y busca equivalencia en rows comparando por id y por fecha.
        // SI no existe un row con los datos, agrega el registro a rows
        incidencias.each{incidencia->
            def existe = rows.find{it.id == incidencia.id && it.fecha == incidencia.fecha}
            if (!existe){
                rows << incidencia
            }
        }
        // Obtiene solamente los id de row
        def datosTodos=[]
        def trabajadoresRows = rows.collect{it.id}.unique()
        //Este trabajador

        trabajadoresRows.each{trbId->
            //def esteTrabajador = rows.find{trbw->trbw.id == trbId}[0]
            // Filtra rows buscando trabajador por Id y obtiene el primer registro
            def esteTrabajador = rows.find{it.id == trbId}



            [
                    "01","02","03","04","05","06","07","08","09","10",
                    "11","12","13","14","15","16","17","18","19","20",
                    "21","22","23","24","25","26","27","28","29","30","31"
            ].each{dia->
                // Calcula los dias que tiene el mes
                Calendar calendar = Calendar.getInstance()
                calendar.setTime(dat.fechaDe)
                def dias =  calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                def esteDia = dia.toInteger()
                if (esteDia <= dias){
                    //console.log(dias)
                    def existe = rows.find{it.id == trbId && it.dia == dia}
                    if (existe){
                        datosTodos << existe
                    }else{
                        // Genera una fecha con el dia y mes y el año
                        def estaFecha = new java.text.SimpleDateFormat("dd-MM-yyyy").parse(dia+"-"+params.mesFilter+"-"+params.yearFilter)
                        datosTodos << [
                                total                   : 0,
                                nombre_completo         : esteTrabajador.nombre_completo,
                                curp                    : esteTrabajador.curp,
                                id                      : esteTrabajador.id,
                                rfc                     : esteTrabajador.rfc,
                                apellido_paterno        : esteTrabajador.apellido_paterno,
                                apellido_materno        : esteTrabajador.apellido_materno,
                                nombres                 : esteTrabajador.nombres,
                                clave_pago              : esteTrabajador.clave_pago,
                                horario_texto           : esteTrabajador.horario_texto,
                                jornada                 : esteTrabajador.jornada,
                                //jid                     : esteTrabajador.jid,
                                observaciones_reporte   : esteTrabajador.observaciones_reporte,
                                observaciones           : esteTrabajador.observaciones,
                                relacion                : esteTrabajador.relacion,
                                //rid                     : esteTrabajador.rid,
                                codigo                  : esteTrabajador.codigo,
                                descripcion             : esteTrabajador.descripcion,
                                area_laboral            : esteTrabajador.area_laboral,
                                fecha_larga             : new java.text.SimpleDateFormat("EEEE MMMM dd 'de' yyyy").format(estaFecha),
                                horas                   : "",
                                incidencia              : "",
                                incidencia_observaciones: "",
                                fecha                   : new java.text.SimpleDateFormat("dd-MM-yyyy").format(estaFecha),
                                dia                     : new java.text.SimpleDateFormat("dd").format(estaFecha),
                                mes                     : new java.text.SimpleDateFormat("MM").format(estaFecha),
                                anio                    : new java.text.SimpleDateFormat("yyyy").format(estaFecha)
                        ]
                    }
                }

            }
        }
        //println trabajadoresRows

        // Ordena indicencias por id y fecha
        //rows = rows.sort{it.id+it.dia}
        rows = rows.sort{it.apellido_paterno+it.apellido_materno+it.nombres+it.dia}

        // Prepara enviar al reporte
        params._format = 'DOCX'
        params._name = "ReporteMensualRh-2024"
        params._file = "reporteRhMesTodos2"

        // NOTA: el plugin jasper no tiene versión compatible con Grails 6.
        // Mientras se integra un generador de reportes (DOCX/PDF), se devuelve
        // la información del reporte en JSON.
        render ([success:true, format:params._format, name:params._name, data:datosTodos] as JSON)

        //render (rows as JSON)


    }
}

package com.rhumanos

import grails.converters.JSON
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import rhumanos.RhRegistrosK1

import grails.plugin.springsecurity.SpringSecurityUtils


class RhService {

    def serviceMethod() {
        

    }
    
    def serviceReporteAsistencia(paramspage, paramsyearFilter, paramstrabajador){
 
        def mensaje = ''
        def observaciones = ''
        def reg1 = ''
        def reg2 = ''
        def reg3 = ''
        def reg4 = ''
        def reg5 = ''
        def reg6 = ''
        def fechaRegistro = ''
        def idIncidencia = ''
        
        def datos = []
        def registrosMes = [:]
        def registroActual = [:]
//        mensaje = 'Filtrando en anio. Acomodar cada uno de acuerdo a 12 pagias de 31 elementos cada uno'
        //total = 372



        //def fechaInicio = Date.parse("d/M/yyyy", params.fechaAmparada)
        //Obten el mes
        def fechaInicio = Date.parse("d-M-yyyy", 1+'-'+paramspage+'-'+paramsyearFilter)
        //def fechaFin = fechaInicio + 1.month
        def fechaFin = ''
        // Sumale un mes para obtener el dia final
        use (TimeCategory) {
            fechaFin = fechaInicio + 1.month
        }
        // Saca la diferencia de dias
        TimeDuration tiempoDiferencia = TimeCategory.minus(fechaFin,fechaInicio)
        def tiempoDias = tiempoDiferencia.days
        
        def trabajador = rhumanos.RhTrabajadores.get(paramstrabajador)
        
        if (trabajador) {
            
            def r = rhumanos.RegistroAsistencia.findAllByTrabajadorAndFechaAmparadaBetween(trabajador,fechaInicio,fechaFin).collect{
            return [
                id:it.id,
                trabajadorId:trabajador.id,
                trabajadorRfc:trabajador.rfc,
                trabajadorCodigo:trabajador.codigoTrabajador.codigo+' '+trabajador.codigoTrabajador.descripcion,
                trabajadorNombre:trabajador.apellidoPaterno+' '+trabajador.apellidoMaterno+' '+trabajador.nombres,
                trabajadorHorarioAsignado:trabajador.horarioTexto,
                trabajadorObservaciones:trabajador.observacionesReporte,
                fecha:new java.text.SimpleDateFormat("dd-MM-yyyy").format(it.fechaAmparada),
                hora:new java.text.SimpleDateFormat("HH:mm:ss").format(it.fechaAmparada),
                tipoIncidencia:it.tipoIncidencia.id,
                tipoIncidenciaTexto:it.tipoIncidencia.tipoIncidencia,
                tipoRegistro:it.tipoRegistro,
                //observaciones:it.observaciones,
                observaciones:(it.observaciones)?it.observaciones:'',
                creado:it.creado,
                dateCreated:new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(it.dateCreated),
            ]
        }
        
        
        // Saca registros del nuevo checador
        def r2 = rhumanos.RhRegistrosK1.findAllByPersonAndDatetimeBetween(trabajador.id,fechaInicio,fechaFin).collect{
            return [
                id:it.id,
                trabajadorId:trabajador.id,
                trabajadorRfc:trabajador.rfc,
                trabajadorCodigo:trabajador.codigoTrabajador.codigo+' '+trabajador.codigoTrabajador.descripcion,
                trabajadorNombre:trabajador.apellidoPaterno+' '+trabajador.apellidoMaterno+' '+trabajador.nombres,
                trabajadorHorarioAsignado:trabajador.horarioTexto,
                trabajadorObservaciones:trabajador.observacionesReporte,
                
                fecha:it.date,
                hora:it.time,
                tipoIncidencia:100,
                tipoIncidenciaTexto:'Asistencia',
                fechaAmparada:new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(it.datetime),
                
                //tipoIncidencia:it.tipoIncidencia.id,
                tipoRegistro:'1',
                //observaciones:it.observaciones,
                observaciones:'',
                creado:'mmateos',
                dateCreated:new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(it.datetime),
            ]
        }
        
        r = r + r2
        
        for (def i = 1; i <=31; i++) {

            def fechaCalendario = Date.parse("dd-MM-yyyy", i+'-'+paramspage+'-'+paramsyearFilter)
            def fechaCalendarioCadena = ''
            //fechaCalendarioCadena = new java.text.SimpleDateFormat("MMMM dd'<br/>'EEEE").format(fechaCalendario)
            if (paramspage.toInteger() == fechaCalendario.month + 1){
                fechaCalendarioCadena = new java.text.SimpleDateFormat("EEEE '<br/>'dd 'de' MMMM yyyy").format(fechaCalendario)
                
                def counter = 1

                r.eachWithIndex{reg,index ->
                    //println reg

                    if (reg.fecha == new java.text.SimpleDateFormat("dd-MM-yyyy").format(fechaCalendario)){


                        if (reg.tipoRegistro == '1'){
                            switch (counter) {  
                                case 1 :  
                                    reg1 = reg.hora
                                    break  
                                case 2 :  
                                    reg2 = reg.hora
                                    break  
                                case 3:  
                                    reg3 = reg.hora
                                    break  
                                case 4:  
                                    reg4 = reg.hora  
                                    break  
                                case 5:  
                                    reg5 = reg.hora 
                                    break  
                                case 6:  
                                    reg6 = reg.hora  
                                    break      
                            }
                        } else {
                            idIncidencia = reg.id
                            observaciones = '<b>'+reg.tipoIncidenciaTexto+'</b><br/>'+reg.observaciones

                        }
                        counter++
                    }
                }
            } else {
                fechaCalendarioCadena = ''
                observaciones = ''
            }
            
            // Esta fecha para buscar n lo que se clona
            //def estaFecha = new java.text.SimpleDateFormat("dd-MM-yyyy").format(fechaCalendario)
            // clona registros del nuevo checador para sacar datos
            //def rt = r2.clone()
            
            datos=datos + [
                fecha:fechaCalendarioCadena,
                reg1:reg1,
                reg2:reg2,
                reg3:reg3,
                reg4:reg4,
                reg5:reg5,
                reg6:reg6,
                
//                reg1:reg1?reg1:(rt.findAll{it.fecha == estaFecha})?(rt.findAll{it.fecha == estaFecha}[0].hora):'',
//                reg2:reg2?reg2:(rt.findAll{it.fecha == estaFecha})?((rt.findAll{it.fecha == estaFecha}[1])?rt.findAll{it.fecha == estaFecha}[1].hora:''):'',
//                reg3:reg3?reg3:(rt.findAll{it.fecha == estaFecha})?((rt.findAll{it.fecha == estaFecha}[2])?rt.findAll{it.fecha == estaFecha}[2].hora:''):'',
//                reg4:reg4?reg4:(rt.findAll{it.fecha == estaFecha})?((rt.findAll{it.fecha == estaFecha}[3])?rt.findAll{it.fecha == estaFecha}[3].hora:''):'',
//                reg5:reg5?reg5:(rt.findAll{it.fecha == estaFecha})?((rt.findAll{it.fecha == estaFecha}[4])?rt.findAll{it.fecha == estaFecha}[4].hora:''):'',
//                reg6:reg6?reg6:(rt.findAll{it.fecha == estaFecha})?((rt.findAll{it.fecha == estaFecha}[5])?rt.findAll{it.fecha == estaFecha}[5].hora:''):'',
                observaciones:observaciones,
                fechaRegistro:new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaCalendario),
                idIncidencia:idIncidencia,
                //-----------------
                trabajadorId:trabajador.id,
                trabajadorRfc:trabajador.rfc,
                trabajadorCodigo:trabajador.codigoTrabajador.codigo+' '+trabajador.codigoTrabajador.descripcion,
                trabajadorNombre:trabajador.apellidoPaterno+' '+trabajador.apellidoMaterno+' '+trabajador.nombres,
                trabajadorHorarioAsignado:trabajador.horarioTexto,
                trabajadorObservaciones:trabajador.observacionesReporte,

                //-----------------
            ]
            reg1 = ''
            reg2 = ''
            reg3 = ''
            reg4 = ''
            reg5 = ''
            reg6 = ''
            observaciones = ''
            fechaRegistro = ''
            idIncidencia = ''
            
        }
            
        } else {
            println 'El trabajador '+paramstrabajador+' No existe. Favor de verificar'
        }

        
        return datos
    }
    def serviceReporteMensual(paramspage, paramsyearFilter){
        def contador = 0
 
        def mensaje = ''
        def observaciones = ''
        def reg1 = ''
        def reg2 = ''
        def reg3 = ''
        def reg4 = ''
        def reg5 = ''
        def reg6 = ''
        def fechaRegistro = ''
        def idIncidencia = ''
        
        def trabajadorId=''
        def trabajadorRfc=''
        def trabajadorCodigo=''
        def trabajadorNombre=''
        def trabajadorHorarioAsignado=''
        def trabajadorObservaciones=''
        
        
        
        
        def datos = []
        def registrosMes = [:]
        def registroActual = [:]
//        mensaje = 'Filtrando en anio. Acomodar cada uno de acuerdo a 12 pagias de 31 elementos cada uno'
        //total = 372



        // Obtiene fechas de inicio y fin
        
        def fechaInicio = Date.parse("d-M-yyyy", 1+'-'+paramspage+'-'+paramsyearFilter)
        def fechaFin = ''
        // Sumale un mes para obtener el dia final
        use (TimeCategory) {
            fechaFin = fechaInicio + 1.month
        }
        // Saca la diferencia de dias
        TimeDuration tiempoDiferencia = TimeCategory.minus(fechaFin,fechaInicio)
        def tiempoDias = tiempoDiferencia.days
        
        // Obtiene el listado de trabajadores que tienen registradas asistencias en el mes seleccionado
        
        def c = rhumanos.RegistroAsistencia.createCriteria()
        
        def resultsT = c.list {
            between ("fechaAmparada",
                    fechaInicio,
                    fechaFin
                )
            projections {
                groupProperty("trabajador")
            }
            order 'trabajador'
        }
        // De la tabla del checador nuevo
        //def c2 = rhumanos.RegistroAsistencia.createCriteria()
        def c2 = rhumanos.RhRegistrosK1.createCriteria()
        def resultsT2 = c2.list {
            between ("datetime",
                    fechaInicio,
                    fechaFin
                )
            projections {
                groupProperty("person")
            }
            order 'person'
        }
        // resultsT y resultsT2 son listas de ids de trabajador
        def trabajadores = resultsT + resultsT2

        println trabajadores

        // Registros de asistencia del mes para todos los trabajadores,
        // con el mismo formato que usa serviceReporteAsistencia
        def r = rhumanos.RegistroAsistencia.findAllByFechaAmparadaBetween(fechaInicio, fechaFin).collect{
            def t = it.trabajador
            return [
                id: it.id,
                trabajadorId: t.id,
                trabajadorRfc: t.rfc,
                trabajadorCodigo: (t.codigoTrabajador) ? t.codigoTrabajador.codigo+' '+t.codigoTrabajador.descripcion : '',
                trabajadorNombre: t.apellidoPaterno+' '+t.apellidoMaterno+' '+t.nombres,
                trabajadorHorarioAsignado: t.horarioTexto,
                trabajadorObservaciones: t.observacionesReporte,
                fecha: new java.text.SimpleDateFormat("dd-MM-yyyy").format(it.fechaAmparada),
                hora: new java.text.SimpleDateFormat("HH:mm:ss").format(it.fechaAmparada),
                tipoIncidencia: it.tipoIncidencia.id,
                tipoIncidenciaTexto: it.tipoIncidencia.tipoIncidencia,
                tipoRegistro: it.tipoRegistro,
                observaciones: (it.observaciones)?it.observaciones:'',
                creado: it.creado,
                dateCreated: new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(it.dateCreated),
            ]
        }
        
        trabajadores.each{
            //println it
            
            for (def i = 1; i <=31; i++) {

                def fechaCalendario = Date.parse("dd-MM-yyyy", i+'-'+paramspage+'-'+paramsyearFilter)
                def fechaCalendarioCadena = ''
                //fechaCalendarioCadena = new java.text.SimpleDateFormat("MMMM dd'<br/>'EEEE").format(fechaCalendario)
                if (paramspage.toInteger() == fechaCalendario.month + 1){
                    fechaCalendarioCadena = new java.text.SimpleDateFormat("MMMM dd'<br/>'EEEE").format(fechaCalendario)

                    
                    //println r
                    //println it
                    def trabajadorI = it
                    //def r2 = r[0].findAllByTrabajadorId(it[0]) 
                    def r2 = r.findAll{p -> p.trabajadorId == trabajadorI} 
                    def counter = 1
                    r2.eachWithIndex{reg,index ->

                        if (reg.fecha == new java.text.SimpleDateFormat("dd-MM-yyyy").format(fechaCalendario)){


                            if (reg.tipoRegistro == '1'){
                                switch (counter) {  
                                    case 1 :  
                                        reg1 = reg.hora
                                        break  
                                    case 2 :  
                                        reg2 = reg.hora
                                        break  
                                    case 3:  
                                        reg3 = reg.hora
                                        break  
                                    case 4:  
                                        reg4 = reg.hora  
                                        break  
                                    case 5:  
                                        reg5 = reg.hora 
                                        break  
                                    case 6:  
                                        reg6 = reg.hora  
                                        break      
                                }
                            } else {
                                idIncidencia = reg.id
                                observaciones = '<b>'+reg.tipoIncidenciaTexto+'</b><br/>'+reg.observaciones
                                trabajadorId=reg.trabajadorId
                                trabajadorRfc=reg.trabajadorRfc
                                trabajadorCodigo=reg.trabajadorCodigo
                                trabajadorNombre=reg.trabajadorNombre
                                trabajadorHorarioAsignado=reg.trabajadorHorarioAsignado
                                trabajadorObservaciones=reg.trabajadorObservaciones

                            }
                            counter++


                        }
                    }
                } else {
                    fechaCalendarioCadena = ''
                    observaciones = ''
                }
                datos=datos + [
                    fecha:fechaCalendarioCadena,
                    reg1:reg1,
                    reg2:reg2,
                    reg3:reg3,
                    reg4:reg4,
                    reg5:reg5,
                    reg6:reg6,
                    observaciones:observaciones,
                    fechaRegistro:new java.text.SimpleDateFormat("dd/MM/yyyy").format(fechaCalendario),
                    idIncidencia:idIncidencia,
                    //-----------------
                    trabajadorId:trabajadorId,
                    trabajadorRfc:trabajadorRfc,
                    trabajadorCodigo:trabajadorCodigo,
                    trabajadorNombre:trabajadorNombre,
                    trabajadorHorarioAsignado:trabajadorHorarioAsignado,
                    trabajadorObservaciones:trabajadorObservaciones,

                    //-----------------
                ]
                reg1 = ''
                reg2 = ''
                reg3 = ''
                reg4 = ''
                reg5 = ''
                reg6 = ''
                observaciones = ''
                fechaRegistro = ''
                idIncidencia = ''

                trabajadorId=''
                trabajadorRfc=''
                trabajadorCodigo=''
                trabajadorNombre=''
                trabajadorHorarioAsignado=''
                trabajadorObservaciones=''


            }
            
            
        }

        return datos
    }
    
}

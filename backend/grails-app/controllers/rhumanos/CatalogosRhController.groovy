package rhumanos

import com.rhumanos.RhAreaLaboral
import com.rhumanos.RhCodigosPersonal
import com.rhumanos.RhFuncionReal
import com.rhumanos.RhHorarios
import com.rhumanos.RhJornada
import com.rhumanos.RhPatron
import com.rhumanos.RhRelacion
import com.rhumanos.RhTipoIncidencia
import grails.converters.JSON

class CatalogosRhController {

    def index() { }
    def catalogoJornadas(){
        render (com.rhumanos.RhJornada.list() as JSON)
    }
    def catalogoAreas(){
        render (com.rhumanos.RhAreaLaboral.list() as JSON)
    }
    
    def catalogoFunciones(){
        render (com.rhumanos.RhFuncionReal.list() as JSON)
    }
    
    def guardaHorarios(){
        params.horarioTurno = RhJornada.get(params.horarioTurno)
        def h = new RhHorarios(params)
        if (h.save(flush:true)){
            render ([success:'true',estado:'true', root:h] as JSON)
        } else {
            render ([success:'true',estado:'false',root:h.errors.allErrors] as JSON)
        }
    }
    
    def listaHorarios(){
        def h = com.rhumanos.RhHorarios.findAllByHorarioActivoNotEqual(1)
        def datos = h.collect{
            return[
                id:it.id,
                horarioDescripcion:it.horarioDescripcion,
                horarioJornadaHrs:it.horarioJornadaHrs,
                horarioTurno:it.horarioTurno.jornada,
                horarioActivo:it.horarioActivo,
                horarioObservaciones:it.horarioObservaciones,
                
                lunesIn: it.lunesIn,
                martesIn: it.martesIn,
                miercolesIn: it.miercolesIn,
                juevesIn: it.juevesIn,
                viernesIn: it.viernesIn,
                sabadoIn: it.sabadoIn,
                domingoIn: it.domingoIn,
                
                lunesOut: it.lunesOut,
                martesOut: it.martesOut,
                miercolesOut: it.miercolesOut,
                juevesOut: it.juevesOut,
                viernesOut: it.viernesOut,
                sabadoOut: it.sabadoOut,
                domingoOut: it.domingoOut
            ]
        }
        render (datos as JSON)
    }
    
    def catalogoTipoIncidencia(){
        def datos = com.rhumanos.RhTipoIncidencia.findAllByIdLessThanEquals(99)
        render (datos as JSON)
    }
    
    def catalogoRelaciones(){
        def datos = com.rhumanos.RhRelacion.createCriteria().list(){
            if (params.patron){
                eq ("patron",com.rhumanos.RhPatron.get(params.patron as Long))
            }
            if (params.relacion){
                def relacion = params.relacion as Long
                if (relacion < (99 as Long)) {
                    eq ("id",relacion)
                } else {
                    ne ("id",99 as Long)
                    ne ("id",5 as Long)
                }
                //ne ("id",99 as Long)
                //ne ("relacion",com.rhumanos.RhPatron.get(99))
                
            }
        }
        render (datos as JSON)
    }
    def codigosPersonalCorto(){
        //def codigos = com.rhumanos.RhCodigosPersonal.findAllByCodigoLikeOrDescripcionLike('%'+params.query+'%','%'+params.query+'%',[max: params.limit, offset: params.start,sort:"descripcion", order:"desc"])
        def codigos = com.rhumanos.RhCodigosPersonal.createCriteria().list([max: params.limit, offset: params.start,sort:"id", order:"desc"]){
            if (params.query){
                or {
                    like ("codigo",'%'+params.query+'%')
                    like ("descripcion",'%'+params.query+'%')
                }
            }
        }
        def datos = codigos.collect{
            return [
                id:it.id,
                codigo:it.codigo,
                
                patron:it.patron.patron,
                patronId:it.patron.id,
                
                rama:it.rama.rama,
                ramaId:it.rama.id,
                
                relacion:it.relacion.relacion,
                relacionId:it.relacion.id,
                
                descripcion:it.descripcion
            ]
        }
        def total = codigos.totalCount
        render ([root:datos,total:total] as JSON)
        
    }
    
}

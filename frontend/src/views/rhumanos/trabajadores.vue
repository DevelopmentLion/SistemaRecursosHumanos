<template>
    <v-container fluid>
        <v-sheet elevation="1">
            <v-toolbar elevation="1" color="orange">
                <v-btn icon @click="$router.push({ path: '/rhumanos' })">
                    <v-icon>
                        mdi-view-grid
                    </v-icon>
                </v-btn>

                <v-toolbar-title>
                    Trabajadores
                </v-toolbar-title>

                <v-spacer/>

                <v-btn icon size="small" class="mx-1" @click="nuevoTrabajador()">
                    <v-icon>
                        mdi-plus
                    </v-icon>
                </v-btn>
            </v-toolbar>
            <!--
            <v-btn @click="cargaVista('lista')">Lista</v-btn>
            <v-btn @click="cargaVista('nuevo')">Nuevo</v-btn>
            <v-btn @click="cargaVista('edita')">Edita</v-btn>
            <v-btn @click="cargaVista('detalles')">Detalles</v-btn>
            -->
            <div>
                <transition
                    name="fade"
                    mode="out-in"
                >
                    <div :key="vistaActiva">
                        <trabajador-lista
                            v-if="vista.lista"
                            v-model:query-trab="queryTrab"
                            v-model:pagina-trab="paginaTrab"
                            v-model:este-trab="esteTrab"
                            @carga-detalle='cargaVista("detalles")'
                        />

                        <trabajador-nuevo
                            v-if="vista.nuevo"
                            v-model:este-trab="esteTrab"

                        />

                        <trabajador-edita
                            v-if="vista.edita"
                            v-model:este-trab="esteTrab"
                            @carga-detalle='cargaVista("detalles")'
                            @carga-lista='cargaVista("lista")'
                            @guardado='actualizaDatos'
                        />

                        <trabajador-detalles
                            :este-trab="esteTrab"
                            v-if="vista.detalles"
                            @carga-lista="cargaVista('lista')"
                            @edita="cargaVista('edita')"
                        />
                    </div>
                </transition>
            </div>
        </v-sheet>
    </v-container>
</template>

<script>
import TrabajadorLista from '@/views/rhumanos/trabajadorLista.vue'
import TrabajadorNuevo from '@/views/rhumanos/trabajadorNuevo.vue'
import TrabajadorEdita from '@/views/rhumanos/trabajadorEdita.vue'
import TrabajadorDetalles from '@/views/rhumanos/trabajadorDetalles.vue'
export default {
    components:{
        'trabajador-lista':TrabajadorLista,
        'trabajador-nuevo':TrabajadorNuevo,
        'trabajador-edita':TrabajadorEdita,
        'trabajador-detalles':TrabajadorDetalles,
    },
    data:()=>({
        // Para mostrar los páneles
        vista:{
            lista:true,
            nuevo:false,
            edita:false,
            detalles:false,
        },
        // Retiene los datos para cuando se cambia la vista
        queryTrab:'',
        paginaTrab:1,

        // De la vista de listado
        esteTrab:{},
    }),
    computed:{
        vistaActiva(){
            return Object.keys(this.vista).find(k => this.vista[k]) || 'lista'
        }
    },
    methods:{
        actualizaDatos(nt){
            this.esteTrab = nt
        },
        cargaVista(v){
            if (v){
                this.vista = {
                    lista:false,
                    nuevo:false,
                    edita:false,
                    detalles:false,
                }

                this.vista[v] = true
            }
        },
        nuevoTrabajador(){
            this.esteTrab={}
            this.cargaVista('edita')

        },
    }

}
</script>

<style>

</style>
<template>
    <div>
        <v-toolbar elevation="1" color="orange">
            

            <v-toolbar-title>
                <span v-if="esteTrab.id">Editar trabajador {{esteTrab.id}}</span>
                <span v-if="!esteTrab.id">Nuevo trabajador</span>
            </v-toolbar-title>

            <v-spacer/>

        </v-toolbar>
        <v-sheet elevation="2" class="pa-2">
            <v-form
                @submit.prevent="guarda"
                                ref="trabForm"
                v-model="validTrab"
            >

                <v-subheader>Datos PERSONALES</v-subheader>
                <v-row no-gutters> 
                    
                    <v-col cols="12" sm="6" md="4" lg="3">
                        <v-checkbox
                            true-value='1'
                            false-value='0'
                            class="px-2"
                            v-model="trb.activo"
                            label="Activo"
                        />
                    </v-col>

                    <v-col cols="12" sm="6" md="4" lg="3" v-for="itm in camposPersonales" :key="itm.campo">
                        <v-text-field
                            class="px-2"
                            v-model="trb[itm.campo]"
                            :label="itm.etiqueta"
                            variant="outlined"
                            :rules="[v => !!v || 'Requerido']"
                        />
                    </v-col>

                </v-row>


                <v-subheader>Datos LABORALES</v-subheader>
                <v-row no-gutters>

                    <v-col cols="12" sm="6" md="6" lg="4">
                        <v-autocomplete
                            class="px-2"
                            v-model="trb.codigoTrabajador"
                            :items="listaCodigos"
                            :loading="loadingCodigos"
                            label="Código del trabajador"
                            v-model:search="queryCodigo"
                            item-title="codigo"
                            item-value="id"
                            no-filter
                            variant="outlined"
                            :rules="[v => !!v || 'Requerido']"
                        >
                            <!-- El template para la lista de los datos -->
                            <template #item="{ props, item }">
                                <v-list-item v-bind="props">
                                    <v-list-item-title>({{ item.codigo }}) {{ item.descripcion }}</v-list-item-title>
                                </v-list-item>
                            </template>
                        </v-autocomplete>

                    </v-col>

                    <v-col cols="12" sm="6" md="6" lg="4">
                        <v-select
                            class="px-2"
                            v-model="trb.relacionLaboral"
                            label="Relación laboral"
                            :items='listaRelLab'
                            item-title="relacion"
                            item-value="id"
                            variant="outlined"
                            :rules="[v => !!v || 'Requerido']"
                        />
                    </v-col>

                    <v-col cols="12" sm="6" md="6" lg="4">
                        <v-select
                            class="px-2"
                            v-model="trb.areaLaboral"
                            label="Área de adscripción"
                            :items='listaAreas'
                            item-title="areaLaboral"
                            item-value="id"
                            variant="outlined"
                            :rules="[v => !!v || 'Requerido']"
                        />
                        
                    </v-col>

                    <v-col cols="12" sm="6" md="6" lg="4">
                        <v-select
                            class="px-2"
                            v-model="trb.funcionReal"
                            label="Función que desempeña"
                            :items='listaFunciones'
                            item-title="funcionReal"
                            item-value="id"
                            variant="outlined"
                            :rules="[v => !!v || 'Requerido']"
                        />
                    </v-col>

                    <v-col cols="12" sm="6" md="6" lg="4">
                        <v-text-field
                            class="px-2"
                            v-model="trb.clavePago"
                            label="Clave de pago"
                            variant="outlined"
                            :rules="[v => !!v || 'Requerido']"
                        />
                    </v-col>

                    <v-col cols="12" sm="6" md="6" lg="4">
                        <v-select
                            class="px-2"
                            v-model="trb.turno"
                            label="Turno"
                            :items='listaJornadas'
                            item-title="jornada"
                            item-value="id"
                            variant="outlined"
                            :rules="[v => !!v || 'Requerido']"
                        />
                    </v-col>

                    <v-col cols="12" sm="6" md="6" lg="4">
                        <v-text-field
                            class="px-2"
                            v-model="trb.horarioTexto"
                            label="Horario"
                            variant="outlined"
                            :rules="[v => !!v || 'Requerido']"
                        />
                    </v-col>

                </v-row>


                <v-subheader>Datos VARIOS</v-subheader>


                <v-row no-gutters>
                    <v-col cols="12">
                        <v-text-field
                            class="px-2"
                            v-model="trb.observacionesReporte"
                            label="Observaciones (las que aparecen en el reporte)"
                            variant="outlined"
                        />
                        <v-textarea
                            class="px-2"
                            v-model="trb.observaciones"
                            label="Observaciones generales"
                            variant="outlined"
                        />
                    </v-col>
                </v-row>
                <div class="d-flex justify-end">
                    <v-btn
                    variant="text"
                    color="error"
                    @click="cancela"
                >
                    Cancelar
                </v-btn>
                
                <v-btn
                    :loading="guardandoTrab"
                    color="success"
                    
                    :disabled="!validTrab"
                    type='submit'
                >
                    Guardar
                </v-btn>
                </div>
            </v-form>
        </v-sheet>
        
    </div>
</template>

<script>
export default {
    mounted(){
        // Clona los datos para poder manipularlos
        this.trb={...this.esteTrab}

        // Carga listaCodigos con el actual
        if (this.esteTrab.id){
            this.listaCodigos = [{id:this.esteTrab.codigoTrabajador, codigo:this.esteTrab.codigoTrabajadorCode, descripcion:this.esteTrab.codigoDesc}]
        }

        // Carga el catálogo de relación laboral
        this.cargaRelacionLaboral()

        // Carga catálogo de áreas de adscripción
        this.cargaAdscripcion()

        // Carga catálogo de Funciones
        this.cargaFunciones()

        // Carga catálogo de Funciones
        this.cargaJornadas()

    },
    props:{
        esteTrab:Object    
    },
    data:()=>({
        trb:{},

        // Validación del formulario
        validTrab:false,
        guardandoTrab:false,

        camposPersonales:[
            {etiqueta:'Nombres', campo:'nombres'},
            {etiqueta:'Primer apellido', campo:'apellidoPaterno'},
            {etiqueta:'Segundo apellido', campo:'apellidoMaterno'},
            {etiqueta:'R.F.C.', campo:'rfc'},
            {etiqueta:'C.U.R.P.', campo:'curp'},
            {etiqueta:'Teléfonos', campo:'telefonos'},
            {etiqueta:'Correo electrónico', campo:'email'},
        ],

        // Para el filtro de códigos
        // Trabajador
        listaCodigos:[],
        loadingCodigos:false,
        queryCodigo:'',
        pageCodigo:1,

        // Para el catálogo de relaciones laborales
        loadingRelLab:false,
        listaRelLab:[],

        // Para el catálogo de áreas de adscripción
        loadingAreas:false,
        listaAreas:[],

        // Para el catálogo de áreas de adscripción
        loadingFunciones:false,
        listaFunciones:[],

        // Para el catálogo de horarios
        loadingJornadas:false,
        listaJornadas:[],

        
    }),
    methods:{
        // Cancela y regresa a listado
        cancela(){
            this.$refs.trabForm.reset()
            if (this.esteTrab.id){
                this.$emit('cargaDetalle')
            } else {
                this.$emit('cargaLista')
            }
        },
        // Carga para el combo de relaciones laborales
        async cargaAdscripcion(){
            this.loadingAreas=true
            try{
                let rl = await this.$http({
                    url:'/catalogosRh/catalogoAreas',
                })

                this.loadingAreas=false
                this.listaAreas = rl.data

            } catch(err) {
                this.loadingAreas=true
                this.$store.dispatch('security/revisaError',err)
            }
        },

        async cargaCodigo(/*query*/){
            if (this.queryCodigo && this.queryCodigo.length >= 1){

                this.loadingCodigos = true

                let params = {}
                params.query = this.queryCodigo
                params.pageCodigo = 1
                params.start = 0
                params.limit = 20
    
                try {
                    var resp = await this.$http
                    .get('/catalogosRh/codigosPersonalCorto',{
                        params:params,
                    })
                    this.listaCodigos = resp.data.root
                    this.loadingCodigos = false
                } catch(err) {
                    this.loadingCodigos = false
                    this.$store.dispatch('security/revisaError',err)
                }
            }
        },
        // Carga carálogo de funciones
        async cargaFunciones(){
            this.loadingFunciones=true
            try{
                let rl = await this.$http({
                    url:'/catalogosRh/catalogoFunciones',
                })

                this.loadingFunciones=false
                this.listaFunciones = rl.data

            } catch(err) {
                this.loadingFunciones=true
                this.$store.dispatch('security/revisaError',err)
            }
        },

        // Carga carálogo de Horarios
        async cargaJornadas(){
            this.loadingJornadas=true
            try{
                let rl = await this.$http({
                    url:'/catalogosRh/catalogoJornadas',
                })

                this.loadingJornadas=false
                this.listaJornadas = rl.data

            } catch(err) {
                this.loadingJornadas=true
                this.$store.dispatch('security/revisaError',err)
            }
        },


        // Carga para el combo de relaciones laborales
        async cargaRelacionLaboral(){
            this.loadingRelLab=true
            try{
                let rl = await this.$http({
                    url:'/catalogosRh/catalogoRelaciones',
                })

                this.loadingRelLab=false
                this.listaRelLab = rl.data

            } catch(err) {
                this.loadingRelLab=true
                this.$store.dispatch('security/revisaError',err)
            }
        },
        // Guarda y regresa a listado
        async guarda(){
            this.guardandoTrab=true
            try{
                let tr = await this.$http({
                    method:'POST',
                    url:'rhumanos/guardaTrabajadorV2',
                    data:this.trb
                })

                if (tr.data.estado == true){

                    this.$swal.fire({
                        icon: 'success',
                        title: 'Guardado',
                        text: 'Registro guardado exitosamente',
                    })

                    this.$emit('guardado', tr.data.root)

                    this.$refs.trabForm.reset()
                    this.$emit('cargaDetalle')
                }
                else {
                    this.$swal.fire({
                        icon: 'warning',
                        title: 'No se puede guardar',
                        text: 'Verifique los datos e intente de nuevo',
                    })
                }
            }catch(err){
                this.guardandoTrab=false
                this.$store.dispatch('security/revisaError',err)
            }
            //console.log(this.trb)
            
            // Actualiza esteTrab con el array que se va a recibir
            // Mandar el comando para que se recargue el listado
        },
        
    },
    watch:{
        queryCodigo(){
            // Para la carga de códigos
            if (this.queryCodigo){
                this.pageCodigo = 1
                this.cargaCodigo()
                //
            }
        },
    },
    
}
</script>

<style>

</style>
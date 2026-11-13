<template>
    <v-dialog
        v-model="dialog"
        max-width="600"
        persistent
    >
        <v-form
            @submit.prevent="guarda"
                        ref="inciForm"
            v-model="validInci"
        >
            <v-card>
                <v-card-title
                                   >
                    Agregar incidencia
                </v-card-title>

                <v-card-text class="pt-3 ">
                    <v-row no-gutters> 
                        <v-col cols="12">

                            <!-- El autocomplete para los medicamentos -->
                            <v-autocomplete
                                v-model="datosInci.trabajador"
                                :items="listaTrabajador"
                                :loading="loadingTrab"
                                label="Trabajador"
                                v-model:search="listaEmpleado"
                                item-title="nombres"
                                item-value="id"
                                :rules="[v => !!v || 'Requerido']"
                                no-filter
                            >
                                <!-- El template para la lista de los datos -->
                                <template #item="{ props, item }">
                                    <v-list-item v-bind="props">
                                        <v-list-item-title>({{ item.id }}) {{ item.nombres }} {{ item.apellidoPaterno }} {{ item.apellidoMaterno }}</v-list-item-title>
                                        <v-list-item-subtitle>{{ item.curp }}</v-list-item-subtitle>
                                    </v-list-item>
                                </template>

                                <!-- El template para el control de paginate -->
                            </v-autocomplete>


                        </v-col>
                        <v-col cols="12" sm="6" md="6" lg="6">
                            <v-select
                                class="px-1"
                                v-model="datosInci.tipoIncidencia"
                                label="Tipo de incidencia"

                                :items='tipoIncidencia'
                                item-title="tipoIncidencia"
                                item-value="id"
                                :rules="[v => !!v || 'Requerido']"
                            ></v-select>
                        </v-col>
                        <v-col cols="12" sm="6" md="6" lg="6">
                            <!-- Campo para la fecha -->
                            <v-text-field
                                class="px-1"
                                v-model="datosInci.fechaAmparada"
                                label="Fecha amparada"
                                append-inner-icon="mdi-calendar-month"
                                @click:append-inner="abreCalendario"
                                :rules="[v => !!v || 'Requerido',
                                        v => /^(\d{1,2})\/(\d{1,2})\/(\d{4})$/.test(v) || 'El formato debe ser dd/mm/aaaaa']"

                            ></v-text-field>
                            <!-- Menú con el picker de fecha -->
                            <v-dialog
                                ref="dialog"
                                v-model="menuFecha"
                                width="290px"
                            >
                                <v-date-picker
                                    v-model="fechaPicker"
                                                                                                            locale="es"
                                                                        @update:model-value="menuFecha = false"
                                >

                                </v-date-picker>
                            </v-dialog>


                        </v-col>
                        <v-col cols="12">
                            <v-text-field
                                class="px-1"
                                v-model="datosInci.observaciones"
                                label="Observaciones"
                            ></v-text-field>
                        </v-col>
                    </v-row>
                </v-card-text>
                <v-card-actions>
                    <v-spacer/>

                    <v-btn
                        variant="text"
                        color="error"
                        @click="cierra"
                    >
                        Cerrar
                    </v-btn>

                    <v-btn
                        :loading="guardandoInci"
                        color="success"

                        :disabled="!validInci"
                        type='submit'
                    >
                        Guardar
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-form>
    </v-dialog>
</template>

<script>
export default {
    mounted(){
        this.cargaTipoIncidencia()
    },
    props:{
        dialogInci:Boolean,
    },
    data:()=>({
        validInci:false,
        guardandoInci:false,
        datosInci:{
            trabajador:null,
            tipoIncidencia:'',
            observaciones:null,
            fechaAmparada:null, //Tiene que ser en formato dd/mm/aaaa
            tipoRegistro:2,
        },
        tipoIncidencia:[],

        // Para el autocomplete de trabajador
        listaTrabajador:[],
        loadingTrab:false,
        listaEmpleado:'',
        page:1,
        params:{},

        // Para el menu de la fecha
        menuFecha:false,
        fechaPicker:'',
    }),
    methods:{
        abreCalendario(){
            this.menuFecha = true
            //console.log('abriendo calendario')
        },

        // ciera agregar incidencia
        cierra(){
            //console.log(this.datosInci)
            //console.log('cierra')
            this.$refs.inciForm.reset()
            this.dialog=false
        },
        // guarda agregar inicidencia
        async guarda(){
            this.guardandoInci = true
            try{
                let inci = await this.$http({
                    url:'/rhumanos/guardaIncidenciaV2',
                    method:'POST',
                    data: this.datosInci
                })
                this.guardandoInci = false
                if (inci.data.estado === true ){

                    this.$refs.inciForm.reset()
                    this.dialog=false

                    this.$swal.fire({
                        icon: 'success',
                        title: 'Guardado',
                        text: 'Registro guardado exitosamente',
                    })
                    this.$emit('guardado')

                } else {
                    this.$swal.fire({
                        icon: 'warning',
                        title: 'No se puede guardar',
                        text: 'Verifique los datos e intente de nuevo',
                    })
                }
            }catch(err){
                this.guardandoInci = false
                this.$store.dispatch('security/revisaError',err)
            }
            this.dialog=false
        },

        // Carga catálogo de tipops de incidencias
        async cargaTipoIncidencia(){
            try{
                let ti = await this.$http({
                    url:'/catalogosRh/catalogoTipoIncidencia',
                    method:'GET',
                })
                this.tipoIncidencia = ti.data
            }catch(err){
                this.$store.dispatch('security/revisaError',err)
            }
        },
        async cargaTrabajador(/*query*/){
         //console.log('cargando')

            if (this.listaEmpleado && this.listaEmpleado.length >= 1){

                this.loadingTrab = true
                this.params.listaEmpleado = this.listaEmpleado
                this.params.page = this.page
                this.params.start = (this.page -1) * 10
                this.params.limit = 10

                try {
                    var resp = await this.$http
                    .get('/rhumanos/listTrabajadores',{

                        params:this.params,
                    })
                    //.then(response =>{
                    this.listaTrabajador = resp.data.root
                    this.total = resp.data.total
                    this.loadingTrab = false
                } catch(err) {
                    this.loadingTrab = false
                    this.$store.dispatch('security/revisaError',err)
                }
            }
        },
    },
    computed:{
        dialog:{
            get(){
                return this.dialogInci
            },
            set(val){
                //this.$refs.inciForm.reset()
                this.$emit('update:dialogInci',val)
            },


        },
        pageLength(){
            return (Math.ceil(this.total / 5));
        },
    },
    watch: {
        // Vigila un cambio en la unidad para iniciar la busqueda
        listaEmpleado(){
            if (this.listaEmpleado){
                // Hace esto para que cuando sea numero lo jale como SRING (muestra error)
                //this.listaEmpleado = String(this.listaEmpleado)
                this.page = 1
                this.cargaTrabajador()
                //
            }
        },
        page(){
            this.cargaTrabajador()
        },
        'datosInci.fechaAmparada'(){
            if (this.datosInci.fechaAmparada){
                let prueba1 =this.datosInci.fechaAmparada.match(/^(\d{1,2})-(\d{1,2})-(\d{4})$/)
                if (prueba1){
                    this.datosInci.fechaAmparada = prueba1[1]+'/'+prueba1[2]+'/'+prueba1[3]
                }
            }
        },
        fechaPicker(){
            //console.log(this.fechaPicker)
            let prueba = this.fechaPicker.match(/^(\d{4})-(\d{1,2})-(\d{1,2})$/)
            //console.log(prueba)
            if (prueba){
                //console.log(prueba)
                this.datosInci.fechaAmparada = prueba[3]+'/'+prueba[2]+'/'+prueba[1]
            }
        }
    },
}
</script>

<style>

</style>
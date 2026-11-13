<template>
    <v-container fluid>
        <v-sheet elevation="1">
            <v-toolbar elevation="1" color="primary">
                <v-btn icon @click="$router.push({ path: '/rhumanos' })">
                    <v-icon>
                        mdi-view-grid
                    </v-icon>
                </v-btn>

                <v-toolbar-title>
                    Incidencias
                </v-toolbar-title>

                <v-spacer/>

                <v-btn icon size="small" class="primary mx-1" @click="muestraFiltro=!muestraFiltro">
                    <v-icon size="small">
                        mdi-magnify
                    </v-icon>
                </v-btn>
                <!--
                <v-btn icon size="small" class="green mx-1" @click="dialogSube=true">
                    <v-icon small>
                        mdi-file-upload-outline
                    </v-icon>
                </v-btn>
                -->
                <v-btn
                    icon
                    size="small"
                    class="green mx-1"
                    @click="dialogInciMul=true"
                >
                    <v-icon>
                        mdi-plus
                    </v-icon>
                </v-btn>
                <!--
                <v-menu offset-y>
                    <template v-slot:activator="{ on, attrs }">
                        <v-btn
                            icon
                            
                            small
                            class="green mx-1"
                            v-bind="attrs"
                            v-on="on"
                        >
                            <v-icon>
                                mdi-plus
                            </v-icon>
                        </v-btn>
                    </template>
                    <v-list>
                        <v-list-item @click="dialogInci=true">
                            <v-list-item-title>Simple</v-list-item-title>
                        </v-list-item>
                        <v-list-item @click="dialogInciMul=true">
                            <v-list-item-title>Múltiple</v-list-item-title>
                        </v-list-item>
                    </v-list>
                </v-menu>
                -->
            </v-toolbar>
            <!-- Panel para buscar -->
            <v-expand-transition>
                <v-sheet v-if="muestraFiltro" class="bg-blue-lighten-5 pa-5">
                    <v-row no-gutters>
                        <v-col cols="12" sm="6" md="6" lg="6" class="px-10">
                            <v-date-picker
                                v-model="dates"
                                multiple
                                hide-header
                                locale="es"
                            ></v-date-picker>
                        </v-col>

                        <v-col cols="12" sm="6" md="6" lg="6" class="pa-1">
                            <v-text-field
                                clearable
                                class="px-1"
                                v-model="dateRangeText"
                                label="Fechas"
                                readonly
                            >
                            </v-text-field>
                            <v-select
                                class="px-1"
                                v-model="params.tipoIncidencia"
                                label="Tipo de incidencia"
                                clearable
                                :items='tipoIncidencia'
                                item-title="tipoIncidencia"
                                item-value="id"
                            ></v-select>

                            <v-autocomplete
                                v-model="params.trabajador"
                                :items="listaTrabajador"
                                :loading="loadingTrab"
                                label="Trabajador"
                                v-model:search="listaEmpleado"
                                item-title="nombres"
                                item-value="id"
                                clearable
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

                            <div class="d-flex justify-end">
                                <!--
                                <v-btn text class="orange mx-1" @click="descarga"><v-icon>mdi-download</v-icon>Descargar</v-btn>
                                -->
                                <v-btn variant="text" class="blue mx-1" @click="filtra">Filtrar<v-icon >mdi-filter</v-icon></v-btn>
                            </div>

                        </v-col>

                    </v-row>
                </v-sheet>
            </v-expand-transition>

            <!-- ---------------------------
            La tabla con los datos
            ------------------------------- -->

            <v-data-table
                item-value="id"
                :loading="loadingInc"
                loading-text="Cargando... espere"
                :headers="headers"
                :items="datosInci"
                :items-per-page="-1"
                hide-default-footer
                            >
                <!-- ---------------------------
                Slot de NO HAY DATOS
                ------------------------------- -->
                <template v-slot:no-data>
                    <span>No se encontraron resultados...</span>
                </template>

            </v-data-table>

            <!-- ---------------------------
            Paginación
            ------------------------------- -->
            <div v-if="!loadingInc">
                <v-pagination
                    v-model="page"
                    :length="pageLength"
                    total-visible="7"
                    @update:model-value="carga"
                ></v-pagination>
                <div class="text-center text-caption">
                    <b>{{pageDe}}</b> a <b>{{pageA}}</b> de <b>{{total}}</b>
                </div>
            </div>
        </v-sheet>

        <!-- Modal para agregar nueva incidencia -->
        <!--
        <nueva-incidencia
            :dialogInci.sync="dialogInci"
            @guardado="carga"
        />
        -->
        <nueva-incidencia-multi
            v-model:dialog-inci="dialogInciMul"
            @guardado="carga"
        />
        <sube-registros
            v-model:dialog-sube="dialogSube"
        />

    </v-container>
</template>

<script>
//import NuevaIncidencia from '@/components/rhumanos/nuevaIncidencia.vue'
import NuevaIncidenciaMulti from '@/components/rhumanos/nuevaIncidenciaMultiple.vue'
import SubeRegistros from '@/components/rhumanos/subeRegistros.vue'
export default {

    mounted(){
        this.carga()
        this.cargaTipoIncidencia()
    },

    components:{
        //'nueva-incidencia':NuevaIncidencia,
        'nueva-incidencia-multi':NuevaIncidenciaMulti,
        'sube-registros':SubeRegistros,
    },


    data:()=>({

        headers: [
            {
                title: 'Empleado',
                key: 'trabajadorNombre',
                sortable:false,
            },

            {
                title: 'Tipo',
                key: 'tipoIncidenciaDetalle',
                sortable:false,
                width:  "20%",
                align:'center',
            },
            {
                title: 'Fecha',
                key: 'fechaAmparada',
                sortable:false,
            },
            {
                title: 'Observaciones',
                key: 'observaciones',
                sortable:false,
                width:  "20%",
                align:'center',
            },
        ],

        loadingInc:false,
        datosInci:[],
        totalInci:0,

        // Para la paginación
        total:0,
        start:0,
        page:1,
        limit:25,

        params:{
            trabajador:null,
            tipoRegistro:null,
            fechaDe:null,
            fechaA:null,

        },

        // Agregar incidencia
        dialogInci:false,
        dialogInciMul:false,
        dialogUpload:false,

        // Sube registros
        dialogSube:false,

        // Filtrado de registros
        muestraFiltro:false,
        // Trabajador
        listaTrabajador:[],
        loadingTrab:false,
        listaEmpleado:'',
        pageTrab:1,

        // Tipos de incidencia
        tipoIncidencia:[],

        // Filtro de fechas
        dates:[],



    }),

    methods:{
        async carga(){
            this.loadingInc=true
            this.datosInci=[]
            /*
            let params = {
                q:this.query,
            }
            */

            this.params.page = this.page
            this.params.start = (this.page -1) * this.limit
            this.params.limit = this.limit
            this.params.tipoRegistro = 2

            try {
                let pac = await this.$http({
                    url:'/rhumanos/listIncidencias',
                    method:'GET',
                    params:this.params,
                })
                this.total = pac.data.total
                this.datosInci = pac.data.root
                this.loadingInc=false
            }catch(err){
                this.loadingInc=false
                this.$store.dispatch('security/revisaError',err)
            }

        },
        // COMBO de tipo de incidencia
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

        // Para la búsqueda
        async cargaTrabajador(/*query*/){
            if (this.listaEmpleado && this.listaEmpleado.length >= 1){

                this.loadingTrab = true

                let params = {}
                params.listaEmpleado = this.listaEmpleado
                params.page = 1
                params.start = 0
                // Este es para la lista de trabajadores
                params.limit = 10

                try {
                    var resp = await this.$http
                    .get('/rhumanos/listTrabajadores',{
                        params:params,
                    })
                    //.then(response =>{
                    this.listaTrabajador = resp.data.root
                    //this.total = resp.data.total
                    this.loadingTrab = false
                } catch(err) {
                    this.loadingTrab = false
                    this.$store.dispatch('security/revisaError',err)
                }
            }
        },
        descarga(){
            console.log('descarga')
        },
        filtra(){

            let f1 = this.dates[0]
            let f2 = this.dates[1]

            if (f1){
                f1 = f1.split('-')
                //console.log(f1)
                this.params.fechaDe =  f1[2]+'-'+f1[1]+'-'+f1[0] 
            }
            if (f2){
                f2 = f2.split('-')
                //console.log(f2)
                this.params.fechaA = f2[2]+'-'+f2[1]+'-'+f2[0]
            }
            this.page = 1

            this.carga()
        },

    },
    computed: {
        /* -----------------
        Computados para la paginación
        -------------------- */
        pageLength: function(){
            return (Math.ceil(this.total / this.limit));
        },
        pageDe: function(){
            let pd = 0

            if (this.total > 0) {
                pd = ((this.page - 1) * this.limit)+1
            }

            return pd

        },
        pageA: function(){
            let pa = this.page * this.limit

            if (pa > this.total ){
                pa = this.total
            }
            return (pa)
        },
        dateRangeText () {
            let texto = ''
            let f1 = this.dates[0]
            let f2 = this.dates[1]

            if (f1){
                f1 = f1.split('-')
                //console.log(f1)
                texto = f1[2]+'-'+f1[1]+'-'+f1[0] 
            }
            if (f2){
                f2 = f2.split('-')
                //console.log(f2)
                texto = 'De '+texto+' a '+ f2[2]+'-'+f2[1]+'-'+f2[0]
            }

            return texto
        },
        fechasTodas(){
            return ([])
        }
        // Para la paginación del nombre

        /* -----------------
        --------------------
        -------------------- */
    },
    watch:{
        listaEmpleado(){
            if (this.listaEmpleado){
                // Hace esto para que cuando sea numero lo jale como SRING (muestra error)
                //this.listaEmpleado = String(this.listaEmpleado)
                this.pageTrab = 1
                this.cargaTrabajador()
                //
            }
        },
        dates(){},
        muestraFiltro(){
            if (this.muestraFiltro === false){
                this.params={
                    trabajador:null,
                    tipoRegistro:null,
                    fechaDe:null,
                    fechaA:null,
                }
            }
            this.carga()

        },
    },
}
</script>

<style>

</style>
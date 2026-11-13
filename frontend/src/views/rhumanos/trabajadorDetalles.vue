<template>
    <div>
        <v-toolbar elevation="1" color="orange">
            <v-btn icon @click="$emit('carga-lista')">
                <v-icon>
                    mdi-arrow-left
                </v-icon>
            </v-btn>

            <v-toolbar-title>
                ({{esteTrab.id}}) {{esteTrab.nombres}} {{esteTrab.apellidoPaterno}} {{esteTrab.apellidoMaterno}}
            </v-toolbar-title>

            <v-spacer/>

            <v-btn icon size="small" class="mx-1" @click="$emit('edita')">
                <v-icon>
                    mdi-pencil
                </v-icon>
            </v-btn>
        </v-toolbar>

        <v-tabs
            v-model="tab"
            bg-color="orange"
        >
            <v-tab
                
                value="0"
            >
                Personales
            </v-tab>
            <v-tab
                
                value="1"
            >
                Laborales
            </v-tab>
            <v-tab
                
                value="2"
            >
                Varios
            </v-tab>
        
        </v-tabs>
        <v-sheet elevation="2" class="pa-2">
            <v-window v-model="tab">
                <v-window-item
                    value="0"
                >
                    <v-row no-gutters> 
                        <v-col cols="12" sm="6" md="6" lg="6" v-for="itm in datosPersonales" :key="itm.campo" class="py-2">
                            <div class="d-block"><b>{{itm.etiqueta}}: </b></div>
                            <div class="d-block">{{esteTrab[itm.campo]}}</div>
                        </v-col>
                    </v-row>
                </v-window-item>
                <v-window-item
                    value="1"
                >
                    <v-row no-gutters> 
                        <v-col cols="12" sm="6" md="6" lg="6" v-for="itm in datosLaborales" :key="itm.campo" class="py-2">
                            <div class="d-block"><b>{{itm.etiqueta}}: </b></div>
                            <div class="d-block">{{esteTrab[itm.campo]}}</div>
                        </v-col>
                    </v-row>
                    
                </v-window-item>
                <v-window-item
                    value="2"
                >
                    <v-row no-gutters> 
                        <v-col cols="12" v-for="itm in datosVarios" :key="itm.campo" class="py-2">
                            <div class="d-block"><b>{{itm.etiqueta}}: </b></div>
                            <div class="d-block">{{esteTrab[itm.campo]}}</div>
                        </v-col>
                    </v-row>
                </v-window-item>
            </v-window>
        </v-sheet>

        <v-toolbar elevation="1" color="orange">
            <v-toolbar-title>
                Registros de asistencia
            </v-toolbar-title>

            <v-spacer/>
 
        </v-toolbar>
        <v-sheet elevation="2">
            <v-row no-gutters class="px-2"> 
                <v-col cols="12" sm="6" md="6" lg="6" class="py-2">
                    <v-select
                        class="px-5"
                        v-model="busqueda.mesFilter"
                        label="Mes"
                        :items='meses'
                        item-title="mes"
                        item-value="id"
                    ></v-select>
                </v-col>
                <v-col cols="12" sm="6" md="6" lg="6" class="py-2">
                    <v-text-field
                        class="px-5"
                        type="number"
                        v-model="busqueda.yearFilter"
                        label="Año"
                    >

                    </v-text-field>
                    
                </v-col>
            </v-row>
            <!-- Botoón para filtrar las incidencias -->
            <v-btn class="primary mx-2" @click="carga()">
                Buscar
            </v-btn>

            <!-- Menú para imprimir las incidencias -->
            <v-menu>
                <template #activator="{ props }">
                    <v-btn v-bind="props" :disabled="reporteTrabajador.length === 0" color="primary">
                        <v-icon>mdi-download</v-icon>
                    </v-btn>
                </template>

                <v-list>
                    <v-list-item
                        v-for="(item, index) in datosFormato"
                        :key="index"
                        @click="imprimeRegistro(item.val)"
                    >
                        <v-list-item-title><v-icon class="pr-2">{{item.icon}}</v-icon> {{ item.text }}</v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-menu>




            <v-data-table
                item-value="id"
                :loading="loadingRep"
                loading-text="Buscando... espere"
                :headers="headers"
                :items="reporteTrabajador"
                :items-per-page="-1"
                                hide-default-footer
                
            >
                <template v-slot:[`item.fecha`]="{ item }">
                    <span v-html="item.fecha"></span>
                </template>

                <template v-slot:[`item.observaciones`]="{ item }">
                    <span v-html="item.observaciones"></span>
                </template>


                <template v-slot:[`item.actions`]="{ item }">
                    <v-icon
                        v-if="!item.idIncidencia"
                        class="mx-1"
                        @click="nuevaIncidencia(item)"
                    >
                        mdi-plus
                    </v-icon>

                    <v-icon
                        v-if="item.idIncidencia"
                        class="mx-1"
                        @click="nuevaIncidencia(item)"
                    >
                        mdi-pencil
                    </v-icon>
                </template>

            </v-data-table>
        </v-sheet>
        
        <!-- Ventana para agregar incidencia -->

        <v-dialog
            v-model="dialogInci"
            max-width="600"
            persistent
        
        >
            <v-card>
                <v-card-title
                                    >
                    Agregar incidencia
                </v-card-title>

                <v-card-text class="pt-3 ">

                    <div>
                        <b>Empleado:</b>
                    </div>
                    <div>
                        ({{addInci.incidenciaEmpleado}}) {{addInci.trabajadorNombre}}
                    </div>

                    <v-select
                        class="mt-3"
                        v-model="addInci.incidenciaTipoIncidencia"
                        label="Tipo de incidencia"

                        :items='tipoIncidencia'
                        item-title="tipoIncidencia"
                        item-value="id"
                        
                    ></v-select>

                    <div>
                        <div>
                            <b>Fecha amparada:</b>

                        </div>
                        <div>
                            {{addInci.incidenciaFechaAmparada}}

                        </div>

                        <v-text-field
                            class="mt-3"
                            v-model="addInci.incidenciaObservaciones"
                            label="Observaciones"
                        ></v-text-field>
                    </div>
                </v-card-text>

                <v-card-actions>
                    <v-spacer/>
                    <v-btn
                        variant="text"
                        color="error"
                        @click="dialogInci=false"
                    >
                        Cancelar
                    </v-btn>
                    
                    <v-btn
                        :loading="guardandoInci"
                        color="success"
                        @click="guardaInci"
                    >
                        Guardar
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>

<script>
export default {
    mounted(){
        this.busqueda.mesFilter = new Date().getMonth()+1
        this.busqueda.yearFilter = new Date().getFullYear()
        this.cargaTipoIncidencia()
    },
    props:{
        esteTrab:Object
    },
    data:()=>({
        tab:0,
        datosPersonales:[
            {etiqueta:'R.F.C.', campo:'rfc'},
            {etiqueta:'C.U.R.P.', campo:'curp'},
            {etiqueta:'Teléfonos', campo:'telefonos'},
            {etiqueta:'Correo electrónico', campo:'email'},
        ],
        datosLaborales:[
            {etiqueta:'Código', campo:'codigoTrabajadorCode'},
            {etiqueta:'Código (descripción)', campo:'codigoTrabajadorDesc'},
            {etiqueta:'Clave presupuestal', campo:'clavePago'},
            {etiqueta:'Relación', campo:'relacionLaboralDesc'},
            {etiqueta:'Turno', campo:'turnoDesc'},
            {etiqueta:'Área', campo:'areaLaboralDesc'},
            {etiqueta:'Horario', campo:'horarioTexto'},
            {etiqueta:'Función', campo:'funcionRealDesc'},
        ],
        datosVarios:[
            {etiqueta:'Observaciones', campo:'observaciones'},
            {etiqueta:'Observaciones (reporte)', campo:'observacionesReporte'},
        ],
        meses:[
            {id:1, mes:'Enero'},
            {id:2, mes:'Febrero'},
            {id:3, mes:'Marzo'},
            {id:4, mes:'Abril'},
            {id:5, mes:'Mayo'},
            {id:6, mes:'Junio'},
            {id:7, mes:'Julio'},
            {id:8, mes:'Agosto'},
            {id:9, mes:'Septiembre'},
            {id:10, mes:'Octubre'},
            {id:11, mes:'Noviembre'},
            {id:12, mes:'Diciembre'},
        ],

        
        busqueda:{
            mesFilter:1,
            yearFilter:2014,
        },

        headers: [
            {
                title: 'Fecha',
                key: 'fecha',
                sortable:false,
                width:'10%'
            },
            {
                title: '',
                key: 'actions',
                sortable:false,
                width:'8%'
            },
            {
                title: 'Detalles',
                key: 'observaciones',
                sortable:false,
                width:'30%'
            },
            {
                title: 'Reg 1',
                key: 'reg1',
                sortable:false,
                width:'7%'
            },
            {
                title: 'Reg 2',
                key: 'reg2',
                sortable:false,
                width:'7%'
            },
            {
                title: 'Reg 3',
                key: 'reg3',
                sortable:false,
                width:'7%'
            },
            {
                title: 'Reg 4',
                key: 'reg4',
                sortable:false,
                width:'7%'
            },
            {
                title: 'Reg 5',
                key: 'reg5',
                sortable:false,
                width:'7%'
            },
            {
                title: 'Reg 6',
                key: 'reg6',
                sortable:false,
                width:'7%'
            },
        ],
        loadingRep:false,
        reporteTrabajador:[],
        tipoIncidencia:[],

        // Para el modal de agregar incidencia
        dialogInci:false,
        guardandoInci:false,
        addInci:{
            incidenciaEmpleado:'',
            incidenciaId:null,
            incidenciaTipoIncidencia:null,
            incidenciaFechaAmparada:null,
            incidenciaObservaciones:null,
        },

        datosFormato:[
            {val:'PDF', text: 'PDF', icon:'mdi-file-pdf'},
            {val:'DOCX', text: 'Ms Word', icon:'mdi-file-word'},
            {val:'XLSX', text: 'Ms Excel', icon:'mdi-file-excel'},
        ],

    }),
    methods:{
        async carga(){

            this.loadingRep = true

            this.busqueda.trabajador = this.esteTrab.id
            this.busqueda.page = 1
            this.busqueda.start = 0
            this.busqueda.limit = 31

            try{
                let rp = await this.$http({
                    method:'GET',
                    url:'/rhumanos/listRegistrosTrabajador',
                    params:this.busqueda,
                })
                this.loadingRep = false
                this.reporteTrabajador = rp.data.root
            }catch(err){
                this.loadingRep = false
                this.$store.dispatch('security/revisaError',err)
            }
        },
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
        nuevaIncidencia(itm){
            this.addInci.incidenciaEmpleado = itm.trabajadorId
            this.addInci.trabajadorNombre = itm.trabajadorNombre
            this.addInci.incidenciaFechaAmparada = itm.fechaRegistro
            this.addInci.incidenciaId = itm.idIncidencia
            this.dialogInci=true
        },
        async guardaInci(){
            this.guardandoInci = true

                let datos = {
                    id:this.addInci.incidenciaId?this.addInci.incidenciaId:null,
                    incidenciaEmpleado:this.addInci.incidenciaEmpleado,
                    incidenciaTipoIncidencia:this.addInci.incidenciaTipoIncidencia,
                    incidenciaFechaAmparada:this.addInci.incidenciaFechaAmparada,
                    incidenciaObservaciones:this.addInci.incidenciaObservaciones,
                }

            try{
                let inci = await this.$http({
                    url:'/rhumanos/editaIncidenciaV2',
                    method:'POST',
                    data: datos
                })
                this.guardandoInci = false
                if (inci.data.estado === true ){
                    this.dialogInci=false
                    this.carga()

                } else {
                    this.$swal.fire({
                        icon: 'warning',
                        title: 'No se puede guardar',
                        title: 'Verifique los datos e intente de nuevo',
                    })
                }
            }catch(err){
                this.guardandoInci = false
                this.$store.dispatch('security/revisaError',err)
            }
            this.dialog=false
        },
        imprimeRegistro(formato){
            //this.busqueda.mesFilter = new Date().getMonth()+1
            //this.busqueda.yearFilter = new Date().getFullYear()

            let pa = {
                trabajador:this.esteTrab.id,
                yearFilter:this.busqueda.yearFilter,
                mesFilter:this.busqueda.mesFilter,
                tipoFormato:formato
            }
            
            window.open(this.$baseUrl+'rhumanos/printRegistro/?mesFilter='+pa.mesFilter+'&yearFilter='+pa.yearFilter+'&trabajador='+pa.trabajador+'&tipoFormato='+pa.tipoFormato)
            
        },
    },
    watch:{
        async dialogInci(){
            
            if (this.dialogInci === true && this.addInci.incidenciaId){
                try{
                    let inc = await this.$http({
                        method:'POST',
                        url:'/rhumanos/getDatosIncidencia',
                        params:{id:this.addInci.incidenciaId},
                    })
                    //console.log(inc)
                    this.addInci.incidenciaTipoIncidencia=inc.data[0].incidenciaTipoIncidencia
                    this.addInci.incidenciaObservaciones=inc.data[0].incidenciaObservaciones
                }
                catch(err){
                    this.$store.dispatch('security/revisaError',err)
                }
            }
            else {
                this.addInci.incidenciaTipoIncidencia = null
                this.addInci.incidenciaObservaciones = null
            }
        },
    },

}
</script>

<style>

</style>
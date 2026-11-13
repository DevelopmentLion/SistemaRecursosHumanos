<template>
    <div>
        <v-toolbar elevation="1" color="green">
            <v-toolbar-title>
                    Todos por mes (Salud)
            </v-toolbar-title>
        </v-toolbar>
        <v-form
            class=" pa-3"
            @submit.prevent="bajaReporte"
                        ref="repForm"
            v-model="validRep"
        >
            <v-select
                class="px-2"
                v-model="reporteData.jornada"
                label="Turno"
                :items='listaJornadas'
                item-title="jornada"
                item-value="id"
                variant="outlined"
                :rules="[v => !!v || 'Requerido']"
            />

            <v-select
                class="px-2"
                v-model="reporteData.mesFilter"
                label="Mes"
                :items='meses'
                item-title="mes"
                item-value="id"
                variant="outlined"
                :rules="[v => !!v || 'Requerido']"
            />

            <v-text-field
                class="px-2"
                type="number"
                v-model="reporteData.yearFilter"
                label="Año"
                variant="outlined"
                :rules="[v => !!v || 'Requerido']"
            />

            <v-select
                class="px-2"
                v-model="reporteData.relacion"
                label="Relación laboral"
                :items='listaRelLab'
                item-title="relacion"
                item-value="id"
                variant="outlined"
                :rules="[v => !!v || 'Requerido']"
            />
            <div class="d-flex justify-center">
                <v-btn
                    color="success"
                    :disabled="!validRep"
                    type='submit'
                >
                    Obtener reporte
                </v-btn>
            </div>
        </v-form>
    </div>
</template>

<script>
export default {
    mounted(){
        this.reporteData={
            jornada: null,
            mesFilter: null,
            yearFilter: null,
            relacion: null
        },
        this.reporteData.mesFilter = new Date().getMonth()+1
        this.reporteData.yearFilter = new Date().getFullYear()
        this.cargaRelacionLaboral()
        this.cargaJornadas()
    },
    data:()=>({

        validRep:false,

        // Para el catálogo de relaciones laborales
        loadingRelLab:false,
        listaRelLab:[],

        // Para el catálogo de horarios
        loadingJornadas:false,
        listaJornadas:[],

        reporteData:{
            jornada: null,
            mesFilter: null,
            yearFilter: null,
            relacion: null
        },
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

    }),
    methods:{
        bajaReporte(){
            let queryString = Object.keys(this.reporteData).map(key => key + '=' + this.reporteData[key]).join('&');
            let rutaRep = this.$baseUrl+'rhumanos/printMes/?'+queryString
            
            window.open(rutaRep)
        },
        async cargaJornadas(){
            this.loadingJornadas=true
            try{
                let rl = await this.$http({
                    url:'catalogosRh/catalogoJornadas',
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
                    url:'catalogosRh/catalogoRelaciones',
                })

                this.loadingRelLab=false
                this.listaRelLab = rl.data

            } catch(err) {
                this.loadingRelLab=true
                this.$store.dispatch('security/revisaError',err)
            }
        },
    }
}
</script>

<style>

</style>
<template>
    <v-container fluid>
        <v-toolbar elevation="1" color="green">
            <v-btn icon @click="$router.push({ path: '/rhumanos' })">
                <v-icon>
                    mdi-view-grid
                </v-icon>
            </v-btn>

            <v-toolbar-title>
                Reportes
            </v-toolbar-title>

            <v-spacer/>
        </v-toolbar>

        <v-sheet elevation="2">
            <v-row no-gutters>
                <v-col cols="12" md="6">
                    <v-treeview
                        class="pa-2"
                        :items="reportes"
                        item-title="name"
                        item-value="id"
                        item-children="children"
                        open-on-click
                        activatable
                        hoverable
                        v-model:activated="reporteActivo"
                        color="green"
                    />
                </v-col>

                <v-col cols="12" md="6">
                    <v-sheet elevation="1">
                        <reporte-101
                            v-if="reporteActivo[0]==101"
                        />
                    </v-sheet>
                </v-col>
            </v-row>
        </v-sheet>
    </v-container>
</template>

<script>
import Reporte101 from '@/components/rhumanos/reportes/reporte101.vue'
export default {
    components:{
        'reporte-101':Reporte101,
    },
    data:()=>({
        reporteActivo:[],
        reportes:[
            {
                id:10,
                name:'Salud',
                children:[
                    {
                        id:101,
                        name: 'Todos por mes'
                    },
                ]
            }
        ]
    }),
    methods:{
        cargaReporte(){
            console.log(this.reporteSeleccionado)
        }
    },
    watch:{

        reporteActivo(){
            //console.log(this.reporteActivo[0])
            switch(this.reporteActivo[0]){
                case 101:{
                    console.log('Cargando el primer valor')
                    break
                }
            }
        },
    }
}
</script>

<style>

</style>

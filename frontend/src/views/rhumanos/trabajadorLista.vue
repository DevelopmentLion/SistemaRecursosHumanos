<template>
    <div>
        <v-text-field
            class="px-10 pb-5"
            v-model="listaEmpleado"
            label="Buscar"
            single-line
            hide-details
            clearable
            
        ></v-text-field>

        <v-data-table
            item-value="id"
            :loading="loadingTrab"
            loading-text="Buscando... espere"
            :headers="headers"
            :items="listaTrabajador"
            :items-per-page="limit"
            disable-pagination
            hide-default-footer
                        
        >
            <template v-slot:no-data>
                <span>No se encontraron resultados...</span>
            </template>
            
            <template v-slot:item.activo="{ item }">
                <v-checkbox
                    true-value='1'
                    false-value='0'
                    :ripple="false"
                    v-model="item.activo"
                    disabled
                    density="compact"
                />
                
            </template>
            
            <template v-slot:item.nombres="{ item }">
                {{item.apellidoPaterno}} {{item.apellidoMaterno}} {{item.nombres}}
                
            </template>
            
            <template v-slot:item.actions="{ item }">
                <v-icon
                    class="mx-1"
                    @click="cargaDetalle(item)"
                >
                    mdi-account-details
                </v-icon>
            </template>
               
        </v-data-table>

         <div v-if="!loadingTrab">
            <v-pagination
                circle
                v-model="page"
                :length="pageLength"
                total-visible="7"
                
            ></v-pagination>

            <div class="text-center caption">
                <b>{{pageDe}}</b> a <b>{{pageA}}</b> de <b>{{total}}</b>
            </div>
        </div>

    </div>
</template>

<script>
export default {
    props:{
        queryTrab:String,
        paginaTrab:Number,
        esteTrab:Object,
    },
    mounted(){
        this.carga()
    },
    data:()=>({
        loadingTrab:false,
        listaTrabajador:[],
        
        // Para la paginación
        //listaEmpleado:'',
        //page:1,

        total:0,
        start:0,
        limit:25,


        // Para la lista
        headers: [
            {
                title: '',
                key: 'activo',
                sortable:false,
                width:"5%"
            },
            {
                title: 'ID',
                key: 'id',
                sortable:false,
                width:"5%"
            },
            
            {
                title: 'Nombre',
                key: 'nombres',
                sortable:false,
            },
            {
                title: 'Codigo',
                key: 'codigoTrabajadorDesc',
                sortable:false,
            },
            {
                title: 'Turno',
                key: 'turnoDesc',
                sortable:false,
            },
            {
                title: '',
                align:'center',
                key: 'actions',
                width:  "12%",
                sortable:false,
            
            },
        ]
    }),
    methods:{
        async carga(){
                this.listaTrabajador=[]
                this.loadingTrab = true

                let params = {
                    listaEmpleado:this.listaEmpleado,
                    page:this.page,
                    start:(this.page -1) * this.limit,
                    limit:this.limit,
                }
    
                try {
                    var resp = await this.$http
                    .get('/rhumanos/listTrabajadores',{
                        params:params,
                    })

                    this.listaTrabajador = resp.data.root
                    this.total = resp.data.total
                    this.loadingTrab = false

                } catch(err) {
                    this.loadingTrab = false
                    this.$store.dispatch('security/revisaError',err)
                }
        },
        cargaDetalle(itm){
            this.esteTrabajador = itm
            this.$emit('cargaDetalle')
        },
        cargaTodos(){
            this.page = 1
            this.listaEmpleado=''
            this.carga()
        },
    },

    computed:{
        // Para continuar la lista cambiando de pagina
        listaEmpleado:{
            get(){
                return this.queryTrab
            },
            set(val){
                this.$emit('update:queryTrab',val)
            },
        },
        page:{
            //
            get(){
                return Number(this.paginaTrab)
            },
            set(val){
                this.$emit('update:paginaTrab',Number(val))
            },
        },
        // Para el paso de trabajador
        esteTrabajador:{
            //
            get(){
                return this.esteTrab
            },
            set(val){
                this.$emit('update:esteTrab',val)
            },
        },
        // Para la paginación
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
    },

    watch:{
        listaEmpleado(){
            if (!this.loadingTrab){
                // Hace esto para que cuando sea numero lo jale como SRING (muestra error)
                //this.listaEmpleado = String(this.listaEmpleado)
                this.page = 1
                this.carga()
                //
            }

            if (!this.listaEmpleado){
                this.cargaTodos()
            }
        },
        page(v){
            this.page = v
            this.carga()
        }
    },
}
</script>

<style>

</style>
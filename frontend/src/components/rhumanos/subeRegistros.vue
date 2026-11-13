<template>
    <v-dialog
        v-model="dialog"
        max-width="600"
        persistent
    >
        <v-card>
            <v-card-title
                           >
                Subir registros
            </v-card-title>

            <v-card-text class="pt-3 ">
                <!-- Componente para subie el registro -->
                <v-file-input
                    label="Archivo .CHK"
                    accept=".chk"
                    v-model="archivoRegistros"
                    show-size
                    :loading="subiendoRegistros"
                ></v-file-input>
                <v-expand-transition>
                    <v-sheet elevation="1" v-if="muestraMensaje">

                        <v-toolbar flat density="compact" color="primary" class="mb-2">
                            <v-toolbar-title>Resumen del proceso</v-toolbar-title>
                        </v-toolbar>
                        <div class="pa-3" >
                            <b>Archivo: </b>{{msg.archivo}}<br/>
                            <b>Entradas procesadas: </b>{{msg.entradas}}<v-icon v-if="msg.entradas>0" color="success">mdi-check</v-icon><br/>
                            <b>Primer registro: </b>{{msg.inicio}}<br/>
                            <b>Último registro: </b>{{msg.fin}}<br/>
                            <b>Entradas con errores: </b>{{msg.errores}} <v-icon  v-if="msg.errores>0" color="error">mdi-alert-circle-outline</v-icon><br/>
                            <b>Entradas duplicadas: </b>{{msg.duplicadas}} <v-icon  v-if="msg.duplicadas>0" color="orange">mdi-content-copy</v-icon><br/>
                        </div>
                    </v-sheet>
                </v-expand-transition>

            </v-card-text>
            <v-card-actions>
                <v-spacer/>
                
                <v-btn
                    text
                    color="error"
                    :disabled="subiendoRegistros"
                    @click="cierra"
                >
                    Cerrar
                </v-btn>
                
                <v-btn
                    :loading="subiendoRegistros"
                    color="success"
                    @click="guarda"
                    :disabled="!terminaSube"
                >
                    Cargar
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
export default {
    props:{
        dialogSube:Boolean,
    },
    data:()=>({
        archivoRegistros:null,

        subeValid:false,
        subiendoRegistros:false,
        terminaSube:true,
        muestraMensaje:false,
        msg:{
            archivoFecha:'',
            entradasProcesadas:'',
            fechaInicial:'',
            fechaFinal:'',
            errores:'',
            duplicadas:'',
        },
        mensaje:'',
    }),
    methods:{
        cierra(){
            //this.$refs.inciForm.reset()
            this.archivoRegistros = null
            this.muestraMensaje = false
            this.msg={
                archivoFecha:'',
                entradasProcesadas:'',
                fechaInicial:'',
                fechaFinal:'',
                entradasErroneas:'',
                entradasDuplicadas:'',
            }
            this.dialog=false
        },
        async guarda(){
            this.muestraMensaje = false
            this.msg={
                archivoFecha:'',
                entradasProcesadas:'',
                fechaInicial:'',
                fechaFinal:'',
                entradasErroneas:'',
                entradasDuplicadas:'',
            }

            let formData = new FormData()
            formData.append("archivoRegistros", this.archivoRegistros)

            this.subiendoRegistros = true
            //console.log(this.archivoRegistros)
            
            try{
                let ar = await this.$http({
                    method:'POST',
                    timeout: 300000,
                    url: '/rhumanos/archivoRegistros',
                    data:formData,
                })
                this.subiendoRegistros = false
                if (ar.data.estado == true){
                    this.muestraMensaje = true
                    this.msg = ar.data.mensajeCompleto
                }
                else {
                    this.muestraMensaje = true
                    this.mensaje = ar.data.mensaje 
                }
            } catch(err){
                this.subiendoRegistros = false
                this.$store.dispatch('security/revisaError',err)
            }
            //this.dialog=false
        }
    },
    computed:{
        dialog:{
            get(){
                return this.dialogSube
            },
            set(val){
                //this.$refs.inciForm.reset()
                this.$emit('update:dialogSube',val)
            },
        },
    },
    watch:{
        dialog(){
            if (this.dialog === true){
                this.archivoRegistros = null
                this.muestraMensaje = false
                this.msg={
                    archivoFecha:'',
                    entradasProcesadas:'',
                    fechaInicial:'',
                    fechaFinal:'',
                    entradasErroneas:'',
                    entradasDuplicadas:'',
                }
            }
        }
    }
     
}
</script>

<style>

</style>
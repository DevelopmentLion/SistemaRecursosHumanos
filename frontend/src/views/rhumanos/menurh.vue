<template>
    <v-container fluid>
        <v-card>
            <v-img
                class="text-white align-top"
                height="200px"
                gradient="to top right, rgba(100,115,201,.33), rgba(25,32,72,.7)"
                src="/images/rhtop.jpg"
            >
                <v-card-title class="justify-start">
                    Recursos humanos
                </v-card-title>
            </v-img>
            <v-card-text>
                <v-row>
                    <v-col
                        v-for="item in mods"
                        :key="item.id"
                        cols="6"
                        sm="4"
                        md="2"
                        lg="2"
                        xl="2"
                    >
                        <v-hover v-slot="{ hover }">
                            <v-sheet
                                v-ripple
                                class="pa-2 d-flex flex-column justify-center align-center"
                                :elevation="hover ? 5 : 1"
                                height="100"
                                @click="$router.push({ path: item.ruta })"
                            >
                                <v-icon size="70">
                                    {{ item.icon }}
                                </v-icon>
                                {{ item.etiqueta }}
                            </v-sheet>
                        </v-hover>
                    </v-col>
                </v-row>
            </v-card-text>
        </v-card>
    </v-container>
</template>

<script>
export default {
    data: () => ({
        mods: [
            { id: 1, etiqueta: 'Incidencias', ruta: '/rhumanos/incidencias', icon: 'mdi-calendar-month' },
            { id: 2, etiqueta: 'Trabajadores', ruta: '/rhumanos/trabajadores', icon: 'mdi-account-multiple' },
            { id: 3, etiqueta: 'Reportes', ruta: '/rhumanos/reportes', icon: 'mdi-file-chart' },
        ],
    }),
}
</script>

<style>
</style>

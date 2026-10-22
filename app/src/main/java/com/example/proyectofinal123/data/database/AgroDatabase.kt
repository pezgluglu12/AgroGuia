package com.example.proyectofinal123.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyectofinal123.data.database.consultas.AgroDao
import com.example.proyectofinal123.data.database.tablas.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CategoriaEntity::class,
        CultivoEntity::class,
        PreguntaEntity::class,
        RespuestaEntity::class,
        TransicionEntity::class,
        DiagnosticoEntity::class,
        HistorialEntity::class
    ],
    version = 5, // VERSIÓN 5: Expansión a 5 Enfermedades y 5 Ramas
    exportSchema = false
)
abstract class AgroDatabase : RoomDatabase() {

    abstract fun agroDao(): AgroDao

    companion object {
        @Volatile
        private var INSTANCE: AgroDatabase? = null

        fun obtenerBaseDeDatos(context: Context): AgroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgroDatabase::class.java,
                    "agro_guia_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .addCallback(LlenarBaseDeDatosCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class LlenarBaseDeDatosCallback : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        if (database.agroDao().obtenerCategorias().isEmpty()) {
                            poblarDatosIniciales(database.agroDao())
                        }
                    }
                }
            }

            private suspend fun poblarDatosIniciales(dao: AgroDao) {
                // ==========================================
                // 1. CATEGORÍAS
                // ==========================================
                dao.insertarCategorias(
                    listOf(
                        CategoriaEntity(1, "Hortalizas", "Cultivos de huerto tradicionales y frutos"),
                        CategoriaEntity(2, "Hierbas Aromáticas", "Plantas para condimento, infusiones o medicina"),
                        CategoriaEntity(3, "Verduras", "Cultivos de hojas y tallos comestibles")
                    )
                )

                // ==========================================
                // 2. CULTIVOS
                // ==========================================
                val listaCultivos = listOf(
                    CultivoEntity(1, 1, "Tomate", "Sol y riego constante", 101),
                    CultivoEntity(2, 1, "Papa", "Tubérculo subterráneo", 201),
                    CultivoEntity(3, 1, "Yuca", "Raíz tropical resistente", 301),
                    CultivoEntity(4, 2, "Albahaca", "Sensible al exceso de agua", 401),
                    CultivoEntity(5, 2, "Menta", "Crecimiento rápido y rastrero", 501),
                    CultivoEntity(6, 2, "Romero", "Arbustiva leñosa resistente", 601),
                    CultivoEntity(7, 3, "Lechuga", "Hoja verde de ciclo corto", 701),
                    CultivoEntity(8, 3, "Espinaca", "Rica en nutrientes", 801),
                    CultivoEntity(9, 3, "Acelga", "Hojas grandes y pencas", 901)
                )
                dao.insertarCultivos(listaCultivos)

                // ==========================================
                // 3. DIAGNÓSTICOS (5 por cultivo = 45 Enfermedades)
                // ==========================================
                dao.insertarDiagnosticos(
                    listOf(
                        // TOMATE (1-5)
                        DiagnosticoEntity(1, "Tizón Tardío (Hongo)", "Manchas oscuras agresivas.", "Fungicida a base de cobre."),
                        DiagnosticoEntity(2, "Oruga Perforadora (Plaga)", "Hojas comidas y larvas.", "Bacillus thuringiensis."),
                        DiagnosticoEntity(3, "Virus del Mosaico (Virus)", "Hojas deformes y amarillas.", "Elimine la planta, no tiene cura."),
                        DiagnosticoEntity(4, "Pudrición Basal (Hongo de suelo)", "Marchitez desde la base.", "Mejore el drenaje, reduzca riego."),
                        DiagnosticoEntity(5, "Gusano Barrenador (Tallo)", "Galerías dentro del tallo principal.", "Inyecte insecticida ecológico en el tallo."),

                        // PAPA (6-10)
                        DiagnosticoEntity(6, "Tizón Temprano (Hongo)", "Anillos concéntricos en hojas.", "Fungicida preventivo."),
                        DiagnosticoEntity(7, "Escarabajo de la Papa (Plaga)", "Follaje masticado por insectos rayados.", "Trampas o insecticida específico."),
                        DiagnosticoEntity(8, "Enrollamiento de la Hoja (Virus)", "Hojas en forma de cuchara y pálidas.", "Controle los pulgones transmisores."),
                        DiagnosticoEntity(9, "Pie Negro (Bacteria)", "Tallo negro y podrido a ras de suelo.", "Drenaje y uso de semillas sanas."),
                        DiagnosticoEntity(10, "Polilla de la Papa (Tallo/Tubérculo)", "Larvas perforando tallos y papas.", "Aporque alto para proteger el tubérculo."),

                        // YUCA (11-15)
                        DiagnosticoEntity(11, "Mancha Parda (Hongo)", "Manchas marrones secas.", "Retire hojas viejas infectadas."),
                        DiagnosticoEntity(12, "Ácaros / Arañita Roja", "Hojas con punteado blanco y telarañas.", "Acaricida o azufre."),
                        DiagnosticoEntity(13, "Mosaico Africano (Virus)", "Decoloración extrema y poco crecimiento.", "Destruya la planta afectada."),
                        DiagnosticoEntity(14, "Pudrición Radicular (Hongo)", "Marchitez general por raíz podrida.", "Rote el cultivo a suelo seco."),
                        DiagnosticoEntity(15, "Barrenador de Brotes", "Perforaciones en los tallos con aserrín.", "Pode y queme los tallos perforados."),

                        // ALBAHACA (16-20)
                        DiagnosticoEntity(16, "Mildiu (Hongo)", "Pelusa gris en el envés.", "Espacie las plantas y ventile."),
                        DiagnosticoEntity(17, "Minador de Hojas (Plaga)", "Caminos blancos en el tejido.", "Aplaste las larvas o pode la hoja."),
                        DiagnosticoEntity(18, "Deficiencia de Nitrógeno", "Amarillamiento generalizado sin hongos.", "Aplique abono rico en nitrógeno."),
                        DiagnosticoEntity(19, "Fusariosis (Hongo vascular)", "Tallo marchito de un solo lado.", "Desinfecte herramientas."),
                        DiagnosticoEntity(20, "Caracoles/Babosas (Tallo)", "Tallos roídos y rastro de baba.", "Trampas de cerveza o cáscaras de huevo."),

                        // MENTA (21-25)
                        DiagnosticoEntity(21, "Roya (Hongo)", "Pústulas naranjas.", "Corte las hojas a ras de suelo."),
                        DiagnosticoEntity(22, "Pulgón (Plaga)", "Insectos pegajosos en brotes nuevos.", "Jabón potásico."),
                        DiagnosticoEntity(23, "Estrés Hídrico", "Puntas secas y amarillas.", "Ajuste la frecuencia de riego."),
                        DiagnosticoEntity(24, "Pudrición del Cuello", "Tallo negro y blando.", "Evite encharcamientos."),
                        DiagnosticoEntity(25, "Gusano Cortador", "Tallos cortados limpiamente en la base.", "Busque la oruga en la tierra de noche."),

                        // ROMERO (26-30)
                        DiagnosticoEntity(26, "Mildiu Polvoso (Hongo)", "Polvo blanco en hojas.", "Pode para dar luz al centro."),
                        DiagnosticoEntity(27, "Cochinilla Algodonosa", "Masas algodonosas en hojas.", "Limpie con alcohol."),
                        DiagnosticoEntity(28, "Falta de Sol", "Hojas amarillas y caída prematura.", "Mueva a sol directo."),
                        DiagnosticoEntity(29, "Asfixia Radicular (Hongo)", "Planta seca pero con tierra muy mojada.", "Cambie a un sustrato arenoso."),
                        DiagnosticoEntity(30, "Escarabajo del Romero", "Insectos metálicos comiendo tallos.", "Recolección manual temprana."),

                        // LECHUGA (31-35)
                        DiagnosticoEntity(31, "Bremia / Mildiu (Hongo)", "Manchas angulares.", "Riegue solo la tierra, no la hoja."),
                        DiagnosticoEntity(32, "Trips (Plaga)", "Hojas plateadas y deformes.", "Trampas cromáticas azules."),
                        DiagnosticoEntity(33, "Falta de Calcio (Tipburn)", "Bordes de hojas quemados.", "Riego constante y calcio."),
                        DiagnosticoEntity(34, "Esclerotinia (Hongo basal)", "Algodón blanco pudriendo la base.", "Rote cultivos de inmediato."),
                        DiagnosticoEntity(35, "Gusano Alambre", "Perforan el tallo bajo tierra.", "Arare el suelo antes de plantar."),

                        // ESPINACA (36-40)
                        DiagnosticoEntity(36, "Cercospora (Hongo)", "Manchas redondas con borde oscuro.", "Elimine restos vegetales viejos."),
                        DiagnosticoEntity(37, "Mosca Blanca (Plaga)", "Nubes de insectos al mover la planta.", "Trampas amarillas y neem."),
                        DiagnosticoEntity(38, "Virus de la Remolacha", "Hojas encrespadas y amarillas.", "Controle malezas y vectores."),
                        DiagnosticoEntity(39, "Pudrición de Corona", "Centro de la espinaca podrido.", "Plante en camas elevadas."),
                        DiagnosticoEntity(40, "Rosquilla Negra (Oruga)", "Mordeduras en el tallo inferior.", "Bacillus thuringiensis al atardecer."),

                        // ACELGA (41-45)
                        DiagnosticoEntity(41, "Viruela (Hongo)", "Manchas pequeñas grises.", "Arranque hojas viejas."),
                        DiagnosticoEntity(42, "Pulguilla (Plaga)", "Agujeros pequeños como perdigones.", "Tierra de diatomeas."),
                        DiagnosticoEntity(43, "Carencia de Boro", "Hojas deformes y tallos agrietados.", "Aplique fertilizante con micronutrientes."),
                        DiagnosticoEntity(44, "Pudrición Blanda (Bacteria)", "Tallo baboso y maloliente.", "Desinfecte el suelo."),
                        DiagnosticoEntity(45, "Nemátodos", "Nódulos en las raíces y tallo débil.", "Incorpore materia orgánica y rote con caléndula.")
                    )
                )

                // ==========================================
                // 4. GENERADOR ALGORÍTMICO DE 5 RAMAS
                // ==========================================
                val listaPreguntas = mutableListOf<PreguntaEntity>()
                val listaRespuestas = mutableListOf<RespuestaEntity>()
                val listaTransiciones = mutableListOf<TransicionEntity>()

                for (c in 1..9) {
                    val nombre = listaCultivos[c - 1].nombre
                    val diagHongosFollaje = (c - 1) * 5 + 1
                    val diagPlagasFollaje = (c - 1) * 5 + 2
                    val diagVirusNutricion = (c - 1) * 5 + 3
                    val diagPudricionTallo = (c - 1) * 5 + 4
                    val diagPlagasTallo = (c - 1) * 5 + 5

                    val qId = { i: Int -> c * 100 + i }
                    val rId = { i: Int -> c * 1000 + i }
                    val tId = { i: Int -> c * 1000 + i }

                    // --- PREGUNTAS (18 Nodos para 5 caminos de 5 niveles) ---
                    listaPreguntas.apply {
                        add(PreguntaEntity(qId(1), c, "NIVEL 1: ¿En qué zona de $nombre observa el problema principal?"))

                        // Bifurcación Principal Nivel 2
                        add(PreguntaEntity(qId(2), c, "NIVEL 2 (Follaje): ¿Qué tipo de anomalía visual predomina en las hojas?"))
                        add(PreguntaEntity(qId(6), c, "NIVEL 2 (Base/Tallo): ¿Qué aspecto nota en la estructura o tallo de $nombre?"))

                        // RAMA 1: Hongos en Follaje
                        add(PreguntaEntity(qId(3), c, "NIVEL 3 (Manchas): ¿Las manchas avanzan rápido y se ven húmedas o polvorientas?"))
                        add(PreguntaEntity(qId(4), c, "NIVEL 4 (Clima): ¿Comenzó en días de alta humedad o lluvias?"))
                        add(PreguntaEntity(qId(5), c, "NIVEL 5 (Confirmación): ¿El tejido termina necrosándose y secándose?"))

                        // RAMA 2: Plagas en Follaje
                        add(PreguntaEntity(qId(10), c, "NIVEL 3 (Insectos): ¿Ve hojas comidas, perforadas o insectos vivos?"))
                        add(PreguntaEntity(qId(11), c, "NIVEL 4 (Daño): ¿El daño avanza rápido devorando el follaje?"))
                        add(PreguntaEntity(qId(12), c, "NIVEL 5 (Confirmación): ¿Encuentra excrementos negros o telarañas cerca?"))

                        // RAMA 3: Virus/Nutrición en Follaje
                        add(PreguntaEntity(qId(13), c, "NIVEL 3 (Decoloración): ¿Las hojas están amarillas, encrespadas o deformes?"))
                        add(PreguntaEntity(qId(14), c, "NIVEL 4 (Patrón): ¿La planta en general se ve enana o estancada?"))
                        add(PreguntaEntity(qId(15), c, "NIVEL 5 (Confirmación): ¿El síntoma no mejora con riego normal?"))

                        // RAMA 4: Pudrición en Tallo/Raíz
                        add(PreguntaEntity(qId(7), c, "NIVEL 3 (Pudrición): ¿El tallo huele mal, está negro o blando al tacto?"))
                        add(PreguntaEntity(qId(8), c, "NIVEL 4 (Humedad): ¿El suelo ha estado encharcado mucho tiempo?"))
                        add(PreguntaEntity(qId(9), c, "NIVEL 5 (Confirmación): ¿La planta completa colapsó o se marchitó?"))

                        // RAMA 5: Plagas en Tallo/Raíz (El caso que mencionaste)
                        add(PreguntaEntity(qId(16), c, "NIVEL 3 (Barrenadores): ¿Ve agujeros, aserrín, galerías o cortes limpios en el tallo?"))
                        add(PreguntaEntity(qId(17), c, "NIVEL 4 (Interior): Si corta una parte afectada, ¿el tallo está hueco o hay larvas?"))
                        add(PreguntaEntity(qId(18), c, "NIVEL 5 (Confirmación): ¿La planta se quiebra fácilmente por ese punto?"))
                    }

                    // Función para enlazar rápidamente
                    var rIdx = 1
                    fun enlazar(idPreg: Int, texto: String, tipoDest: String, idDest: Int) {
                        listaRespuestas.add(RespuestaEntity(rId(rIdx), qId(idPreg), texto))
                        listaTransiciones.add(TransicionEntity(tId(rIdx), rId(rIdx), tipoDest, idDest))
                        rIdx++
                    }

                    // --- ENRUTAMIENTO ---
                    // Nivel 1 -> Va a Nivel 2 (Follaje o Tallo)
                    enlazar(1, "En las hojas o follaje superior", "PREGUNTA", qId(2))
                    enlazar(1, "En el tallo, raíces o planta entera", "PREGUNTA", qId(6))

                    // Nivel 2 (Follaje) -> Abre Ramas 1, 2 y 3
                    enlazar(2, "Manchas, moho o pudrición en hojas", "PREGUNTA", qId(3)) // Rama 1
                    enlazar(2, "Hojas comidas, picadas o plagas visibles", "PREGUNTA", qId(10)) // Rama 2
                    enlazar(2, "Hojas amarillas, deformes o estancadas", "PREGUNTA", qId(13)) // Rama 3

                    // Nivel 2 (Tallo) -> Abre Ramas 4 y 5
                    enlazar(6, "Marchitez, negrura o base blanda", "PREGUNTA", qId(7)) // Rama 4
                    enlazar(6, "Agujeros, cortes o insectos en el tallo", "PREGUNTA", qId(16)) // Rama 5

                    // --- RAMA 1 (Hongos Follaje -> diagHongosFollaje) ---
                    enlazar(3, "Sí, son manchas húmedas o polvo", "PREGUNTA", qId(4))
                    enlazar(4, "Coincide con lluvia/humedad", "PREGUNTA", qId(5))
                    enlazar(5, "Confirmar: Daño fúngico severo", "DIAGNOSTICO", diagHongosFollaje)

                    // --- RAMA 2 (Plagas Follaje -> diagPlagasFollaje) ---
                    enlazar(10, "Sí, hay daños físicos por mordeduras", "PREGUNTA", qId(11))
                    enlazar(11, "Avanza devorando el follaje", "PREGUNTA", qId(12))
                    enlazar(12, "Confirmar: Presencia de plaga", "DIAGNOSTICO", diagPlagasFollaje)

                    // --- RAMA 3 (Virus/Nutrición -> diagVirusNutricion) ---
                    enlazar(13, "Sí, decoloración y deformación extrema", "PREGUNTA", qId(14))
                    enlazar(14, "La planta está enana o débil", "PREGUNTA", qId(15))
                    enlazar(15, "Confirmar: Problema viral/carencial", "DIAGNOSTICO", diagVirusNutricion)

                    // --- RAMA 4 (Pudrición Tallo -> diagPudricionTallo) ---
                    enlazar(7, "Sí, el tallo se pudre desde abajo", "PREGUNTA", qId(8))
                    enlazar(8, "El suelo está muy húmedo o mal drenado", "PREGUNTA", qId(9))
                    enlazar(9, "Confirmar: Pudrición basal", "DIAGNOSTICO", diagPudricionTallo)

                    // --- RAMA 5 (Plagas Tallo -> diagPlagasTallo) ---
                    enlazar(16, "Sí, el tallo tiene orificios o aserrín", "PREGUNTA", qId(17))
                    enlazar(17, "El interior está dañado o perforado", "PREGUNTA", qId(18))
                    enlazar(18, "Confirmar: Ataque de barrenador", "DIAGNOSTICO", diagPlagasTallo)
                }

                dao.insertarPreguntas(listaPreguntas)
                dao.insertarRespuestas(listaRespuestas)
                dao.insertarTransiciones(listaTransiciones)
            }
        }
    }
}
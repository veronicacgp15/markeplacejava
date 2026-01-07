package com.vgarcia.marketplace.infrastructure.utils;

public class Constans {
    public static final String EMAIL_EXISTS = "El email ya esta en uso:";
    public static final String FILA_ERROR = "Fila {}: Formato de fecha inválido. Se esperaba 'yyyy-MM-dd'. Valor: '{}'";
    public static final String FILA_IGNORADA = "Fila {} ignorada por estar incompleta o vacía.";
    public static final String ERROR_ARCHIVO_CSV = "Error crítico al leer el archivo CSV";
    public static final String ARCHIVO_VACÍO = "El archivo está vacío.";
    public static final String ERROR_INESPERADO_SERVIDOR = "Ocurrió un error inesperado en el servidor: ";
    public static final String LISTADO_DE_CLIENTES = "Listado de Clientes";
    public static final String ERROR_PROCESAR_ARCHIVO_CSV = "Error fatal al procesar el archivo CSV: ";
    public static final String ERROR_GENERAR_EXCEL = "Error al generar el Excel: ";
    public static final String ERROR_GRUPO = "Error en el grupo '";
    public static final String FALLO_AL_PROCESAR_EL_ARCHIVO_CSV = "Fallo al procesar el archivo CSV: ";
    public static final String EL_CLIENT_ID_NO_NULO_EN_EL_GRUPO = "El 'clientId' no puede ser nulo en el grupo '";
    public static final String PRODUCT_ID_NO_PUEDE_SER_NULO = "El 'productId' no puede ser nulo. Revisa la fila del grupo '";
    public static final String LA_QUANTITY_DEBE_SER_NÚMERO_POSITIVO = "La 'quantity' debe ser un número positivo. Revisa la fila del grupo '";
    public static final String CATEGORÍA_PADRE_NO_EXISTE = "La nueva categoría padre no existe.";
    public static final String UNA_CATEGORÍA_NO_PUEDE_SER_SU_PROPIO_PADRE = "Una categoría no puede ser su propio padre.";
    public static final String CATEGORÍA_PADRE_ESPECIFICADA_NO_EXISTE = "La categoría padre especificada no existe.";
    public static final String EXISTE_UN_PRODUCTO_CON_EL_SKU = "Ya existe un producto con el SKU: ";
    public static final String INCONSISTENCIA_SE_INTENTA_CONFIRMAR_UNA_VENTA_POR = "Inconsistencia: se intenta confirmar una venta por ";
    public static final String CANTIDAD_A_AÑADIR_DEBE_SER_POSITIVA = "La cantidad a añadir debe ser positiva.";
    public static final String STOCK_INSUFICIENTE_DISPONIBLE = "Stock insuficiente. Disponible: ";
    public static final String NO_SE_PUDO_PROCESAR_EL_ARCHIVO_CSV_CAUSA = "No se pudo procesar el archivo CSV. Causa: ";
    public static final String NO_SE_ENCONTRARON_PRODUCTOS_EXTERNOS = "No se encontraron productos externos.";
    public static final String NO_SE_ENCONTRÓ_LA_ORDEN_A_ACTUALIZAR_CON_ID = "No se encontró la orden a actualizar con ID: ";
    public static final String EL_PRECIO_BASE_ES_OBLIGATORIO = "El precio base es obligatorio.";
    public static final String LA_TASA_DE_IMPUESTO_ES_OBLIGATORIA = "La tasa de impuesto es obligatoria.";
}

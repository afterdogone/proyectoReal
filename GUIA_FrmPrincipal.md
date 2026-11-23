# Guía para armar `FrmPrincipal` en NetBeans

Sistema POS y Gestión Operativa — **ENEL Servicios de Limpieza**

---

## 1. Estructura de paquetes

```
proyectoReal/src/
└── proyectoreal/
    ├── ProyectoReal.java              ← clase main (arranca la ventana)
    ├── interfaz/
    │   └── InterfazENEL.java          ← constantes: textos, combos, encabezados, mensajes
    ├── modelo/
    │   ├── Persona.java               ← clase abstracta (padre de Cliente y Trabajador)
    │   ├── Cliente.java
    │   ├── Trabajador.java
    │   ├── Servicio.java              ← servicios de limpieza e insumos (con stock)
    │   ├── DetalleVenta.java          ← un renglón del carrito
    │   ├── Venta.java                 ← carrito + totales + ticket
    │   └── Nomina.java                ← un pago de nómina
    ├── controlador/
    │   └── ControladorENEL.java       ← ArrayList estáticos, CRUD y datos precargados
    └── FrmPrincipal.java/.form       ← ventana principal (JFrame, ya armada)
```

Las clases de `interfaz`, `modelo` y `controlador` ya están escritas en el proyecto.

**Lo que se repasa de los proyectos anteriores:** clases y objetos, encapsulamiento (get/set),
herencia y clase abstracta (`Persona`), polimorfismo (`getDescripcion()` / `toString()`),
interfaz de constantes, `ArrayList` y arreglos, ciclos y condicionales, excepciones
(`try/catch`, `NumberFormatException`), validaciones con `matches()`, y Swing con el
diseñador de NetBeans (`JTabbedPane`, `JTable`, `JComboBox`, `JOptionPane`, eventos).

---

## 2. Crear la ventana

> **Ya está hecho:** `proyectoreal/FrmPrincipal` ya tiene todos los componentes, eventos y código.
> Esta sección explica cómo está armada por si quieres modificarla en el diseñador.

1. El JFrame Form es `proyectoreal.FrmPrincipal` (layout *Free Design* de NetBeans).
2. Componentes acomodados con el diseñador; nada con `setBounds`.
3. Arrastra un **Tabbed Pane** que ocupe toda la ventana → nombre de variable: `tabPrincipal`.
4. Arrastra **4 Panel** dentro del `tabPrincipal` (cada panel es una pestaña):
   `pnlVenta`, `pnlClientes`, `pnlNomina`, `pnlCatalogo`.
   (El texto de cada pestaña lo pone el código, no importa lo que diga en el diseñador.)

> **Cómo cambiar el nombre de variable:** clic derecho en el componente → **Change Variable Name...**
>
> **Cómo crear un evento:** doble clic en el botón (crea `ActionPerformed`). Para tablas y cajas
> de texto: clic derecho → **Events → Mouse → mouseClicked** o **Key → keyReleased**.
>
> Las `JTable` se arrastran ya dentro de su `JScrollPane`; solo renombra la tabla.
> Los títulos fijos ("Nombre:", "Teléfono:", etc.) son `JLabel` normales; esos no necesitan nombre.
> No cambies el *model* de tablas ni combos en el diseñador: el código los llena.

---

## 3. Componentes de cada pestaña

### Pestaña 1 — `pnlVenta` (Punto de Venta)

| Componente | Nombre de variable | Para qué |
|---|---|---|
| JComboBox | `cmbClienteVenta` | Cliente o "Público General" |
| JComboBox | `cmbServicioVenta` | Servicio o insumo a vender |
| JTextField | `txtCantidad` | Cantidad / metros cuadrados |
| JButton | `btnAgregarCarrito` | Texto: "Agregar al carrito" |
| JButton | `btnQuitarCarrito` | Texto: "Quitar seleccionado" |
| JTable | `tblCarrito` | Concepto, Cantidad, Precio Unitario, Subtotal |
| JLabel | `lblSubtotal` | Muestra el subtotal |
| JLabel | `lblIva` | Muestra el IVA 16 % |
| JLabel | `lblTotal` | Muestra el total |
| JComboBox | `cmbMetodoPago` | Efectivo, Transferencia, Tarjeta |
| JTextField | `txtMontoRecibido` | Dinero que entrega el cliente |
| JLabel | `lblCambio` | Muestra el cambio |
| JButton | `btnCobrar` | Texto: "Cobrar" |
| JTextArea | `txaTicket` | Ticket de la última venta |
| JTable | `tblVentas` | Historial de ventas cobradas |

Eventos: `btnAgregarCarrito`, `btnQuitarCarrito`, `btnCobrar` y `cmbMetodoPago` → **ActionPerformed**;
`txtMontoRecibido` → **keyReleased**.

### Pestaña 2 — `pnlClientes` (Cartera de Clientes)

| Componente | Nombre de variable | Para qué |
|---|---|---|
| JTextField | `txtIdCliente` | ID (número) |
| JTextField | `txtNombreCliente` | Nombre o empresa |
| JTextField | `txtTelefonoCliente` | 10 dígitos |
| JTextField | `txtDireccionCliente` | Dirección |
| JComboBox | `cmbTipoCliente` | Comercial, Residencial, Industrial |
| JButton | `btnAgregarCliente` | "Agregar" |
| JButton | `btnBuscarCliente` | "Buscar" (por ID) |
| JButton | `btnEditarCliente` | "Editar" |
| JButton | `btnEliminarCliente` | "Eliminar" |
| JButton | `btnLimpiarCliente` | "Limpiar" |
| JTable | `tblClientes` | Todos los clientes |

Eventos: los 5 botones → **ActionPerformed**; `tblClientes` → **mouseClicked**.

### Pestaña 3 — `pnlNomina` (Trabajadores y Nómina)

| Componente | Nombre de variable | Para qué |
|---|---|---|
| JTextField | `txtIdTrabajador` | ID (número) |
| JTextField | `txtNombreTrabajador` | Nombre |
| JComboBox | `cmbPuesto` | Operador, Supervisor |
| JTextField | `txtSueldoDia` | Sueldo por día / turno |
| JButton | `btnAgregarTrabajador` | "Registrar trabajador" |
| JButton | `btnEliminarTrabajador` | "Eliminar seleccionado" |
| JTable | `tblTrabajadores` | Lista del personal |
| JComboBox | `cmbTrabajadorNomina` | Trabajador al que se le paga |
| JTextField | `txtDiasTrabajados` | Días (0 a 7) |
| JTextField | `txtHorasExtra` | Horas extra (se pagan dobles) |
| JTextField | `txtDescuentos` | Descuentos en pesos |
| JButton | `btnCalcularPago` | "Calcular y Registrar Pago" |
| JTable | `tblNomina` | Historial de pagos |

Eventos: los 3 botones → **ActionPerformed**.

### Pestaña 4 — `pnlCatalogo` (Servicios e Inventario)

| Componente | Nombre de variable | Para qué |
|---|---|---|
| JTextField | `txtIdCatalogo` | ID (automático, solo lectura) |
| JComboBox | `cmbTipoCatalogo` | Servicio o Insumo |
| JTextField | `txtNombreCatalogo` | Nombre |
| JTextField | `txtPrecioCatalogo` | Precio base |
| JTextField | `txtStockCatalogo` | Stock (solo insumos) |
| JButton | `btnAgregarCatalogo` | "Agregar" |
| JButton | `btnEditarCatalogo` | "Editar" |
| JButton | `btnEliminarCatalogo` | "Eliminar" |
| JButton | `btnLimpiarCatalogo` | "Limpiar" |
| JTable | `tblCatalogo` | Catálogo completo |

Eventos: los 4 botones y `cmbTipoCatalogo` → **ActionPerformed**; `tblCatalogo` → **mouseClicked**.

---

## 4. Código para pegar

Abre la pestaña **Source** de `FrmPrincipal`. Lo gris (*guarded blocks*) no se puede editar
y no hay que tocarlo.

### 4.1 Imports (arriba, debajo de `package proyectoreal;`)

```java
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import proyectoreal.controlador.ControladorENEL;
import proyectoreal.interfaz.InterfazENEL;
import proyectoreal.modelo.Cliente;
import proyectoreal.modelo.DetalleVenta;
import proyectoreal.modelo.Nomina;
import proyectoreal.modelo.Servicio;
import proyectoreal.modelo.Trabajador;
import proyectoreal.modelo.Venta;
```

### 4.2 Atributos, constructor y métodos de apoyo

**Reemplaza** el constructor que generó NetBeans (`public FrmPrincipal() { initComponents(); }`)
por todo este bloque:

```java
    // Venta en curso: funciona como el carrito. Se reinicia despues de cobrar.
    private Venta objVenta = new Venta();

    // Modelos de las tablas (se crean en inicializar())
    private DefaultTableModel modeloCarrito;
    private DefaultTableModel modeloVentas;
    private DefaultTableModel modeloClientes;
    private DefaultTableModel modeloTrabajadores;
    private DefaultTableModel modeloNomina;
    private DefaultTableModel modeloCatalogo;

    public FrmPrincipal() {
        initComponents();
        inicializar();
    }

    /** Prepara la ventana: titulos, tablas, combos y datos precargados. */
    private void inicializar() {
        setTitle(InterfazENEL.TITULO_VENTANA);
        setLocationRelativeTo(null);

        // Titulos de las pestañas
        tabPrincipal.setTitleAt(0, InterfazENEL.TAB_VENTA);
        tabPrincipal.setTitleAt(1, InterfazENEL.TAB_CLIENTES);
        tabPrincipal.setTitleAt(2, InterfazENEL.TAB_NOMINA);
        tabPrincipal.setTitleAt(3, InterfazENEL.TAB_CATALOGO);

        // Tablas con los encabezados de InterfazENEL
        modeloCarrito = crearModelo(InterfazENEL.ENCABEZADO_CARRITO);
        modeloVentas = crearModelo(InterfazENEL.ENCABEZADO_VENTAS);
        modeloClientes = crearModelo(InterfazENEL.ENCABEZADO_CLIENTES);
        modeloTrabajadores = crearModelo(InterfazENEL.ENCABEZADO_TRABAJADORES);
        modeloNomina = crearModelo(InterfazENEL.ENCABEZADO_NOMINA);
        modeloCatalogo = crearModelo(InterfazENEL.ENCABEZADO_CATALOGO);
        tblCarrito.setModel(modeloCarrito);
        tblVentas.setModel(modeloVentas);
        tblClientes.setModel(modeloClientes);
        tblTrabajadores.setModel(modeloTrabajadores);
        tblNomina.setModel(modeloNomina);
        tblCatalogo.setModel(modeloCatalogo);

        // Combos con opciones fijas
        cmbTipoCliente.setModel(new DefaultComboBoxModel<>(InterfazENEL.TIPOS_CLIENTE));
        cmbPuesto.setModel(new DefaultComboBoxModel<>(InterfazENEL.PUESTOS));
        cmbMetodoPago.setModel(new DefaultComboBoxModel<>(InterfazENEL.METODOS_PAGO));
        cmbTipoCatalogo.setModel(new DefaultComboBoxModel<>(InterfazENEL.TIPOS_CATALOGO));

        // Ajustes de los campos
        txaTicket.setEditable(false);
        txtIdCatalogo.setEditable(false);
        txtStockCatalogo.setEnabled(false);   // el primer tipo es "Servicio" (sin stock)
        txtHorasExtra.setText("0");
        txtDescuentos.setText("0");

        // Llenar tablas y combos con los datos del controlador
        cargarTablaCarrito();
        cargarTablaVentas();
        cargarTablaClientes();
        cargarTablaTrabajadores();
        cargarTablaNomina();
        cargarTablaCatalogo();
    }

    // ===================== METODOS DE APOYO =====================

    /** Modelo de tabla cuyas celdas no se pueden editar con doble clic. */
    private DefaultTableModel crearModelo(String[] encabezados) {
        return new DefaultTableModel(encabezados, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, InterfazENEL.TITULO_ERROR, JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, InterfazENEL.TITULO_AVISO, JOptionPane.INFORMATION_MESSAGE);
    }

    /** Pregunta "¿Seguro...?" y regresa true si el usuario contesta Sí. */
    private boolean confirmarEliminar() {
        int respuesta = JOptionPane.showConfirmDialog(this, InterfazENEL.MSG_CONFIRMAR_ELIMINAR,
                InterfazENEL.TITULO_CONFIRMAR, JOptionPane.YES_NO_OPTION);
        return respuesta == JOptionPane.YES_OPTION;
    }

    /** Lee un numero con decimales (>= 0). Si no es valido lanza NumberFormatException con un mensaje claro. */
    private double leerNumero(JTextField txt, String campo) {
        double valor;
        try {
            valor = Double.parseDouble(txt.getText().trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException(InterfazENEL.MSG_NUMERO_INVALIDO + campo);
        }
        if (valor < 0 || Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new NumberFormatException(InterfazENEL.MSG_NUMERO_INVALIDO + campo);
        }
        return valor;
    }

    /** Lee un numero entero (>= 0). Si no es valido lanza NumberFormatException con un mensaje claro. */
    private int leerEntero(JTextField txt, String campo) {
        int valor;
        try {
            valor = Integer.parseInt(txt.getText().trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException(InterfazENEL.MSG_NUMERO_INVALIDO + campo);
        }
        if (valor < 0) {
            throw new NumberFormatException(InterfazENEL.MSG_NUMERO_INVALIDO + campo);
        }
        return valor;
    }

    // ===================== PESTAÑA 1: PUNTO DE VENTA =====================

    private void cargarTablaCarrito() {
        modeloCarrito.setRowCount(0);
        for (DetalleVenta objDetalle : objVenta.getDetalles()) {
            modeloCarrito.addRow(new Object[]{
                objDetalle.getServicio().getNombre(),
                objDetalle.getCantidad(),
                ControladorENEL.dinero(objDetalle.getPrecioUnitario()),
                ControladorENEL.dinero(objDetalle.getSubtotal())
            });
        }
        actualizarTotales();
    }

    private void cargarTablaVentas() {
        modeloVentas.setRowCount(0);
        for (Venta objVentaHecha : ControladorENEL.getVentas()) {
            modeloVentas.addRow(new Object[]{
                objVentaHecha.getFolio(),
                objVentaHecha.getFecha(),
                objVentaHecha.getNombreCliente(),
                objVentaHecha.getMetodoPago(),
                ControladorENEL.dinero(objVentaHecha.getTotal())
            });
        }
    }

    /** Primer renglon "Publico General" y despues los clientes registrados. */
    private void cargarComboClientes() {
        cmbClienteVenta.removeAllItems();
        cmbClienteVenta.addItem(InterfazENEL.PUBLICO_GENERAL);
        for (Cliente objCliente : ControladorENEL.getClientes()) {
            cmbClienteVenta.addItem(objCliente.toString());
        }
    }

    private void cargarComboServicios() {
        cmbServicioVenta.removeAllItems();
        for (Servicio objServicio : ControladorENEL.getServicios()) {
            cmbServicioVenta.addItem(objServicio.toString() + " - " + ControladorENEL.dinero(objServicio.getPrecio()));
        }
    }

    private void actualizarTotales() {
        lblSubtotal.setText(ControladorENEL.dinero(objVenta.getSubtotal()));
        lblIva.setText(ControladorENEL.dinero(objVenta.getIva()));
        lblTotal.setText(ControladorENEL.dinero(objVenta.getTotal()));
        actualizarCambio();
    }

    /** Cambio = recibido - total (si aun no alcanza, muestra $0.00). */
    private void actualizarCambio() {
        double cambio = 0;
        try {
            double recibido = Double.parseDouble(txtMontoRecibido.getText().trim());
            cambio = Math.max(0, recibido - objVenta.getTotal());
        } catch (NumberFormatException e) {
            cambio = 0;   // campo vacio o con letras
        }
        lblCambio.setText(ControladorENEL.dinero(cambio));
    }

    // ===================== PESTAÑA 2: CLIENTES =====================

    private void cargarTablaClientes() {
        modeloClientes.setRowCount(0);
        for (Cliente objCliente : ControladorENEL.getClientes()) {
            modeloClientes.addRow(new Object[]{
                objCliente.getId(),
                objCliente.getNombre(),
                objCliente.getTelefono(),
                objCliente.getDireccion(),
                objCliente.getTipoServicio()
            });
        }
        cargarComboClientes();   // el combo del POS siempre igual que la tabla
    }

    /** Lee y valida el formulario. Regresa null (y avisa) si algo esta mal. */
    private Cliente leerFormularioCliente() {
        int id;
        try {
            id = leerEntero(txtIdCliente, "ID");
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return null;
        }
        String nombre = txtNombreCliente.getText().trim();
        String telefono = txtTelefonoCliente.getText().trim();
        String direccion = txtDireccionCliente.getText().trim();
        if (nombre.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
            mostrarError(InterfazENEL.MSG_CAMPOS_VACIOS);
            return null;
        }
        if (!telefono.matches("\\d{10}")) {
            mostrarError(InterfazENEL.MSG_TELEFONO_INVALIDO);
            return null;
        }
        String tipo = (String) cmbTipoCliente.getSelectedItem();
        return new Cliente(id, nombre, telefono, direccion, tipo);
    }

    private void mostrarCliente(Cliente objCliente) {
        txtIdCliente.setText(String.valueOf(objCliente.getId()));
        txtNombreCliente.setText(objCliente.getNombre());
        txtTelefonoCliente.setText(objCliente.getTelefono());
        txtDireccionCliente.setText(objCliente.getDireccion());
        cmbTipoCliente.setSelectedItem(objCliente.getTipoServicio());
    }

    private void limpiarCliente() {
        txtIdCliente.setText("");
        txtNombreCliente.setText("");
        txtTelefonoCliente.setText("");
        txtDireccionCliente.setText("");
        cmbTipoCliente.setSelectedIndex(0);
        tblClientes.clearSelection();
    }

    // ===================== PESTAÑA 3: TRABAJADORES Y NOMINA =====================

    private void cargarTablaTrabajadores() {
        modeloTrabajadores.setRowCount(0);
        cmbTrabajadorNomina.removeAllItems();
        for (Trabajador objTrabajador : ControladorENEL.getTrabajadores()) {
            modeloTrabajadores.addRow(new Object[]{
                objTrabajador.getId(),
                objTrabajador.getNombre(),
                objTrabajador.getPuesto(),
                ControladorENEL.dinero(objTrabajador.getSueldoDia())
            });
            cmbTrabajadorNomina.addItem(objTrabajador.toString());
        }
    }

    private void cargarTablaNomina() {
        modeloNomina.setRowCount(0);
        for (Nomina objNomina : ControladorENEL.getNominas()) {
            modeloNomina.addRow(new Object[]{
                objNomina.getFolio(),
                objNomina.getFecha(),
                objNomina.getNombreTrabajador(),
                objNomina.getDiasTrabajados(),
                objNomina.getHorasExtra(),
                ControladorENEL.dinero(objNomina.getSueldoBase()),
                ControladorENEL.dinero(objNomina.getPagoHorasExtra()),
                ControladorENEL.dinero(objNomina.getDescuentos()),
                ControladorENEL.dinero(objNomina.getTotalNeto())
            });
        }
    }

    private void limpiarTrabajador() {
        txtIdTrabajador.setText("");
        txtNombreTrabajador.setText("");
        txtSueldoDia.setText("");
        cmbPuesto.setSelectedIndex(0);
    }

    // ===================== PESTAÑA 4: CATALOGO E INVENTARIO =====================

    private void cargarTablaCatalogo() {
        modeloCatalogo.setRowCount(0);
        for (Servicio objServicio : ControladorENEL.getServicios()) {
            modeloCatalogo.addRow(new Object[]{
                objServicio.getId(),
                objServicio.getTipo(),
                objServicio.getNombre(),
                ControladorENEL.dinero(objServicio.getPrecio()),
                objServicio.getStockTexto()
            });
        }
        cargarComboServicios();   // el combo del POS siempre igual que el catalogo
    }

    private void limpiarCatalogo() {
        txtIdCatalogo.setText("");
        txtNombreCatalogo.setText("");
        txtPrecioCatalogo.setText("");
        txtStockCatalogo.setText("");
        cmbTipoCatalogo.setSelectedIndex(0);
        tblCatalogo.clearSelection();
    }
```

### 4.3 Código de los eventos

NetBeans **ya crea la firma** del método cuando haces doble clic en el botón.
Tú solo pega lo que va **entre las llaves** `{ }` (se muestra la firma completa para que ubiques cuál es).

#### Pestaña 1 — Punto de Venta

```java
    private void btnAgregarCarritoActionPerformed(java.awt.event.ActionEvent evt) {
        int indice = cmbServicioVenta.getSelectedIndex();
        if (indice < 0) {
            mostrarError(InterfazENEL.MSG_SELECCIONA_OPCION);
            return;
        }
        Servicio objServicio = ControladorENEL.getServicios().get(indice);

        double cantidad;
        try {
            cantidad = leerNumero(txtCantidad, "Cantidad");
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        if (cantidad <= 0) {
            mostrarError(InterfazENEL.MSG_CANTIDAD_INVALIDA);
            return;
        }

        // Los insumos van por pieza y no pueden pasar del stock disponible
        if (objServicio.esInsumo()) {
            if (cantidad != Math.floor(cantidad)) {
                mostrarError(InterfazENEL.MSG_PIEZA_ENTERA);
                return;
            }
            if (cantidad + objVenta.cantidadDe(objServicio) > objServicio.getStock()) {
                mostrarError(InterfazENEL.MSG_STOCK_INSUFICIENTE + objServicio.getNombre()
                        + " (disponibles: " + objServicio.getStock() + ")");
                return;
            }
        }

        objVenta.agregarDetalle(new DetalleVenta(objServicio, cantidad));
        cargarTablaCarrito();
        txtCantidad.setText("");
    }
```

```java
    private void btnQuitarCarritoActionPerformed(java.awt.event.ActionEvent evt) {
        int fila = tblCarrito.getSelectedRow();
        if (fila < 0) {
            mostrarError(InterfazENEL.MSG_SELECCIONA_FILA);
            return;
        }
        objVenta.quitarDetalle(fila);
        cargarTablaCarrito();
    }
```

```java
    private void cmbMetodoPagoActionPerformed(java.awt.event.ActionEvent evt) {
        // Solo con efectivo se escribe el monto recibido; con tarjeta o transferencia se paga exacto
        boolean esEfectivo = InterfazENEL.METODO_EFECTIVO.equals(cmbMetodoPago.getSelectedItem());
        txtMontoRecibido.setEnabled(esEfectivo);
        if (!esEfectivo) {
            txtMontoRecibido.setText("");
        }
        actualizarCambio();
    }
```

```java
    private void txtMontoRecibidoKeyReleased(java.awt.event.KeyEvent evt) {
        actualizarCambio();
    }
```

```java
    private void btnCobrarActionPerformed(java.awt.event.ActionEvent evt) {
        if (objVenta.estaVacia()) {
            mostrarError(InterfazENEL.MSG_CARRITO_VACIO);
            return;
        }
        String faltante = ControladorENEL.validarStock(objVenta);
        if (faltante != null) {
            mostrarError(InterfazENEL.MSG_STOCK_INSUFICIENTE + faltante);
            return;
        }

        // Monto recibido: en efectivo se valida; con otro metodo se cobra exacto
        String metodoPago = (String) cmbMetodoPago.getSelectedItem();
        double montoRecibido = objVenta.getTotal();
        if (InterfazENEL.METODO_EFECTIVO.equals(metodoPago)) {
            try {
                montoRecibido = leerNumero(txtMontoRecibido, "Monto recibido");
            } catch (NumberFormatException ex) {
                mostrarError(ex.getMessage());
                return;
            }
            if (montoRecibido < objVenta.getTotal()) {
                mostrarError(InterfazENEL.MSG_PAGO_INSUFICIENTE);
                return;
            }
        }

        // Cliente: el renglon 0 es "Publico General"; los demas son la lista de clientes
        int indiceCliente = cmbClienteVenta.getSelectedIndex();
        if (indiceCliente > 0) {
            Cliente objCliente = ControladorENEL.getClientes().get(indiceCliente - 1);
            objVenta.setNombreCliente(objCliente.getNombre());
        } else {
            objVenta.setNombreCliente(InterfazENEL.PUBLICO_GENERAL);
        }

        // Guardar, descontar stock y mostrar el ticket
        ControladorENEL.registrarVenta(objVenta, metodoPago, montoRecibido);
        txaTicket.setText(objVenta.generarTicket());
        txaTicket.setCaretPosition(0);
        mostrarAviso(InterfazENEL.MSG_VENTA_REGISTRADA);

        // Empezar una venta nueva
        objVenta = new Venta();
        txtMontoRecibido.setText("");
        cmbClienteVenta.setSelectedIndex(0);
        cargarTablaCarrito();
        cargarTablaVentas();
        cargarTablaCatalogo();   // el stock cambio
    }
```

#### Pestaña 2 — Clientes

```java
    private void btnAgregarClienteActionPerformed(java.awt.event.ActionEvent evt) {
        Cliente objCliente = leerFormularioCliente();
        if (objCliente == null) {
            return;
        }
        if (!ControladorENEL.agregarCliente(objCliente)) {
            mostrarError(InterfazENEL.MSG_ID_REPETIDO);
            return;
        }
        cargarTablaClientes();
        limpiarCliente();
        mostrarAviso(InterfazENEL.MSG_GUARDADO);
    }
```

```java
    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {
        int id;
        try {
            id = leerEntero(txtIdCliente, "ID");
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        Cliente objCliente = ControladorENEL.buscarCliente(id);
        if (objCliente == null) {
            mostrarError(InterfazENEL.MSG_NO_ENCONTRADO);
            return;
        }
        mostrarCliente(objCliente);
    }
```

```java
    private void btnEditarClienteActionPerformed(java.awt.event.ActionEvent evt) {
        Cliente objCliente = leerFormularioCliente();
        if (objCliente == null) {
            return;
        }
        if (!ControladorENEL.editarCliente(objCliente)) {
            mostrarError(InterfazENEL.MSG_NO_ENCONTRADO);
            return;
        }
        cargarTablaClientes();
        limpiarCliente();
        mostrarAviso(InterfazENEL.MSG_ACTUALIZADO);
    }
```

```java
    private void btnEliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {
        int id;
        try {
            id = leerEntero(txtIdCliente, "ID");
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        if (ControladorENEL.buscarCliente(id) == null) {
            mostrarError(InterfazENEL.MSG_NO_ENCONTRADO);
            return;
        }
        if (confirmarEliminar()) {
            ControladorENEL.eliminarCliente(id);
            cargarTablaClientes();
            limpiarCliente();
            mostrarAviso(InterfazENEL.MSG_ELIMINADO);
        }
    }
```

```java
    private void btnLimpiarClienteActionPerformed(java.awt.event.ActionEvent evt) {
        limpiarCliente();
    }
```

```java
    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {
        // Al dar clic en un renglon, sus datos pasan al formulario
        int fila = tblClientes.getSelectedRow();
        if (fila >= 0) {
            mostrarCliente(ControladorENEL.getClientes().get(fila));
        }
    }
```

#### Pestaña 3 — Trabajadores y Nómina

```java
    private void btnAgregarTrabajadorActionPerformed(java.awt.event.ActionEvent evt) {
        int id;
        double sueldoDia;
        try {
            id = leerEntero(txtIdTrabajador, "ID");
            sueldoDia = leerNumero(txtSueldoDia, "Sueldo por día");
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        String nombre = txtNombreTrabajador.getText().trim();
        if (nombre.isEmpty() || sueldoDia == 0) {
            mostrarError(InterfazENEL.MSG_CAMPOS_VACIOS);
            return;
        }
        String puesto = (String) cmbPuesto.getSelectedItem();
        Trabajador objTrabajador = new Trabajador(id, nombre, puesto, sueldoDia);
        if (!ControladorENEL.agregarTrabajador(objTrabajador)) {
            mostrarError(InterfazENEL.MSG_ID_REPETIDO);
            return;
        }
        cargarTablaTrabajadores();
        limpiarTrabajador();
        mostrarAviso(InterfazENEL.MSG_GUARDADO);
    }
```

```java
    private void btnEliminarTrabajadorActionPerformed(java.awt.event.ActionEvent evt) {
        int fila = tblTrabajadores.getSelectedRow();
        if (fila < 0) {
            mostrarError(InterfazENEL.MSG_SELECCIONA_FILA);
            return;
        }
        if (confirmarEliminar()) {
            Trabajador objTrabajador = ControladorENEL.getTrabajadores().get(fila);
            ControladorENEL.eliminarTrabajador(objTrabajador.getId());
            cargarTablaTrabajadores();
            mostrarAviso(InterfazENEL.MSG_ELIMINADO);
        }
    }
```

```java
    private void btnCalcularPagoActionPerformed(java.awt.event.ActionEvent evt) {
        int indice = cmbTrabajadorNomina.getSelectedIndex();
        if (indice < 0) {
            mostrarError(InterfazENEL.MSG_SELECCIONA_OPCION);
            return;
        }
        Trabajador objTrabajador = ControladorENEL.getTrabajadores().get(indice);

        int dias;
        double horasExtra;
        double descuentos;
        try {
            dias = leerEntero(txtDiasTrabajados, "Días trabajados");
            horasExtra = leerNumero(txtHorasExtra, "Horas extra");
            descuentos = leerNumero(txtDescuentos, "Descuentos");
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        if (dias > 7) {
            mostrarError(InterfazENEL.MSG_DIAS_INVALIDOS);
            return;
        }

        // Se calcula primero para revisar que el neto no quede negativo
        Nomina objNomina = new Nomina(objTrabajador, dias, horasExtra, descuentos);
        if (objNomina.getTotalNeto() < 0) {
            mostrarError(InterfazENEL.MSG_NETO_NEGATIVO);
            return;
        }
        ControladorENEL.registrarNomina(objNomina);
        cargarTablaNomina();

        txtDiasTrabajados.setText("");
        txtHorasExtra.setText("0");
        txtDescuentos.setText("0");
        mostrarAviso(InterfazENEL.MSG_NOMINA_REGISTRADA + ControladorENEL.dinero(objNomina.getTotalNeto()));
    }
```

#### Pestaña 4 — Servicios e Inventario

```java
    private void cmbTipoCatalogoActionPerformed(java.awt.event.ActionEvent evt) {
        // El stock solo aplica a los insumos
        boolean esInsumo = InterfazENEL.TIPO_INSUMO.equals(cmbTipoCatalogo.getSelectedItem());
        txtStockCatalogo.setEnabled(esInsumo);
        if (!esInsumo) {
            txtStockCatalogo.setText("");
        }
    }
```

```java
    private void btnAgregarCatalogoActionPerformed(java.awt.event.ActionEvent evt) {
        String nombre = txtNombreCatalogo.getText().trim();
        String tipo = (String) cmbTipoCatalogo.getSelectedItem();
        double precio;
        int stock = 0;
        try {
            precio = leerNumero(txtPrecioCatalogo, "Precio");
            if (InterfazENEL.TIPO_INSUMO.equals(tipo)) {
                stock = leerEntero(txtStockCatalogo, "Stock");
            }
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        if (nombre.isEmpty() || precio == 0) {
            mostrarError(InterfazENEL.MSG_CAMPOS_VACIOS);
            return;
        }
        ControladorENEL.agregarServicio(nombre, tipo, precio, stock);
        cargarTablaCatalogo();
        limpiarCatalogo();
        mostrarAviso(InterfazENEL.MSG_GUARDADO);
    }
```

```java
    private void btnEditarCatalogoActionPerformed(java.awt.event.ActionEvent evt) {
        if (txtIdCatalogo.getText().isEmpty()) {
            mostrarError(InterfazENEL.MSG_SELECCIONA_FILA);
            return;
        }
        int id = Integer.parseInt(txtIdCatalogo.getText());
        String nombre = txtNombreCatalogo.getText().trim();
        String tipo = (String) cmbTipoCatalogo.getSelectedItem();
        double precio;
        int stock = 0;
        try {
            precio = leerNumero(txtPrecioCatalogo, "Precio");
            if (InterfazENEL.TIPO_INSUMO.equals(tipo)) {
                stock = leerEntero(txtStockCatalogo, "Stock");
            }
        } catch (NumberFormatException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        if (nombre.isEmpty() || precio == 0) {
            mostrarError(InterfazENEL.MSG_CAMPOS_VACIOS);
            return;
        }
        ControladorENEL.editarServicio(id, nombre, tipo, precio, stock);
        cargarTablaCatalogo();
        limpiarCatalogo();
        mostrarAviso(InterfazENEL.MSG_ACTUALIZADO);
    }
```

```java
    private void btnEliminarCatalogoActionPerformed(java.awt.event.ActionEvent evt) {
        if (txtIdCatalogo.getText().isEmpty()) {
            mostrarError(InterfazENEL.MSG_SELECCIONA_FILA);
            return;
        }
        if (confirmarEliminar()) {
            ControladorENEL.eliminarServicio(Integer.parseInt(txtIdCatalogo.getText()));
            cargarTablaCatalogo();
            limpiarCatalogo();
            mostrarAviso(InterfazENEL.MSG_ELIMINADO);
        }
    }
```

```java
    private void btnLimpiarCatalogoActionPerformed(java.awt.event.ActionEvent evt) {
        limpiarCatalogo();
    }
```

```java
    private void tblCatalogoMouseClicked(java.awt.event.MouseEvent evt) {
        int fila = tblCatalogo.getSelectedRow();
        if (fila < 0) {
            return;
        }
        Servicio objServicio = ControladorENEL.getServicios().get(fila);
        txtIdCatalogo.setText(String.valueOf(objServicio.getId()));
        txtNombreCatalogo.setText(objServicio.getNombre());
        txtPrecioCatalogo.setText(String.valueOf(objServicio.getPrecio()));
        cmbTipoCatalogo.setSelectedItem(objServicio.getTipo());   // esto activa o desactiva el stock
        txtStockCatalogo.setText(objServicio.esInsumo() ? String.valueOf(objServicio.getStock()) : "");
    }
```

---

## 5. Arrancar el programa

`ProyectoReal.java` (la clase main del proyecto) ya abre la ventana:

```java
    public static void main(String[] args) {
        FrmPrincipal.main(args);
    }
```

Ejecuta con **F6**. Datos de prueba que ya vienen cargados:

- **Clientes:** ID 1 a 4 (comercial, residencial, industrial).
- **Trabajadores:** ID 101 a 104.
- **Catálogo:** 4 servicios y 3 insumos con stock.
- **Ventas:** 3 ya cobradas. **Nómina:** 3 pagos.

**Cálculo de nómina:** días × sueldo por día + horas extra × (sueldo ÷ 8) × 2 − descuentos.

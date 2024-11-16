package com.example.repasosqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // Variables a utilizar
    EditText txtClientName, txtFoodName, txtOrderPrice, txtOrderID;
    Button btnSaveOrder, btnUpdateOrder, btnDeleteOrder;
    ListView orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Buscamos elementos por su id
        txtClientName = findViewById(R.id.txtClientName);
        txtFoodName = findViewById(R.id.txtFoodName);
        txtOrderPrice = findViewById(R.id.txtOrderPrice);
        txtOrderID = findViewById(R.id.txtOrderID);
        btnSaveOrder = findViewById(R.id.btnSaveOrder);
        btnUpdateOrder = findViewById(R.id.btnUpdateOrder);
        btnDeleteOrder = findViewById(R.id.btnDeleteOrder);
        orderList = findViewById(R.id.ordersList);

        // El campo de texto ID no será accesible, debido a que la id se ingresará
        // gracias al evento onItemClick de la listView.
        txtOrderID.setEnabled(false);

        seeOrders(); // Carga los pedidos de la base de datos

        // Desactivamos los botones ELIMINAR y ACTUALIZAR al arrancar la aplicación
        // (esto debido a que solo se habilitan cuando presionas un elemento de la listView).
        btnUpdateOrder.setEnabled(false);
        btnDeleteOrder.setEnabled(false);


        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String itemSeleccionado = adapterView.getItemAtPosition(position).toString();
                Log.d("DEBUG", "Elemento seleccionado: " + itemSeleccionado);

                try {
                    // Dividimos en diferentes Strings el elemento de la listView
                    String[] partes = itemSeleccionado.split(",");
                    Log.d("DEBUG", "Partes: " + Arrays.toString(partes));

                    // Asegúrate de que hay suficientes partes
                    if (partes.length >= 4) {
                        // partes[0] = "ID: 1", partes[1] = "Cliente: Juan", partes[2] = "Comida: nugget", partes[3] = "Precio: $10"

                        // Tomamos el String que contiene la id del pedido
                        String idParte = partes[0]; // "ID: 1"
                        String idPedido = idParte.split(": ")[1]; // "1"

                        // Mostrar la ID en el EditText correspondiente
                        txtOrderID.setText(idPedido);

                        // partes[1] = "Cliente: Juan" >> "Juan"
                        String parteCliente = partes[1].split(": ")[1];
                        // partes[2] = "Comida: nugget" >> "nugget"
                        String parteComida = partes[2].split(": ")[1];
                        // partes[3] = "Precio: $10" >> "10"
                        String partePrecio = partes[3].split(": \\$")[1];

                        // Mostrar los datos en los campos correspondientes
                        txtClientName.setText(parteCliente);
                        txtFoodName.setText(parteComida);
                        txtOrderPrice.setText(partePrecio);

                        // Habilitamos los botones para ACTUALIZAR y ELIMINAR
                        btnUpdateOrder.setEnabled(true);
                        btnDeleteOrder.setEnabled(true);

                        // Deshabilitamos el botón para GUARDAR pedidos.
                        btnSaveOrder.setEnabled(false);

                    } else {
                        Toast.makeText(MainActivity.this, "El formato del elemento no es válido", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // Manejo de errores
                    Log.e("ERROR", "Error al procesar el elemento seleccionado", e);
                    Toast.makeText(MainActivity.this, "Error al procesar el pedido", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void saveOrder(View view){
        // Conexión de SQLite con la base de datos creada
        AdminSQLite admin = new AdminSQLite(this, "MCDONNALS", null, 1);
        // Obtenemos los datos de la Conexión
        SQLiteDatabase db = admin.getWritableDatabase();

        // Validamos que los campos NO esten vacíos
        if(txtClientName.getText().toString().isEmpty()
        && txtFoodName.getText().toString().isEmpty()
        && txtOrderPrice.getText().toString().isEmpty()){
            Toast.makeText(this, "Rellene los campos", Toast.LENGTH_SHORT).show();
        } else {
            // Extraemos las variables del formulario
            String client = txtClientName.getText().toString();
            String food = txtFoodName.getText().toString();
            Double price = Double.parseDouble(txtOrderPrice.getText().toString());

            // Array de datos que almacenará los datos para realizar el INSERT
            ContentValues orderData = new ContentValues();

            // Insertamos los datos en el Array de datos creado
            orderData.put("NOMBRE_CLIENTE", client);
            orderData.put("COMIDA", food);
            orderData.put("PRECIO", price);

            // Realizamos la insercción de datos
            db.insert("MCDONNALS", null, orderData);

            // Limpiamos los campos
            txtClientName.setText("");
            txtFoodName.setText("");
            txtOrderPrice.setText("");

            Toast.makeText(this, "Pedido guardado con éxito", Toast.LENGTH_SHORT).show();
            seeOrders(); // Actualiza la lista de pedidos
        }
        db.close(); // Cerramos la conexión
    }

    public void seeOrders(){
        // Conexión de SQLite con la base de datos creada
        AdminSQLite admin = new AdminSQLite(this, "MCDONNALS", null,1);
        // Obtenemos los datos de la Conexión
        SQLiteDatabase db = admin.getWritableDatabase();

        // Obtenemos los resultados de la Consulta
        Cursor fila = db.rawQuery("SELECT * FROM MCDONNALS ORDER BY ID_PEDIDO ASC", null);

        // Array que almacenará el contenido de cada registro
        ArrayList<String> registros = new ArrayList<>();

        // Se verifican que hayan registros
        if(fila.moveToFirst()){
            do{
                String id = fila.getString(0);
                String client = fila.getString(1);
                String food = fila.getString(2);
                String price = fila.getString(3);

                String orderInfo = "ID: "+id+", Cliente: "+client+", Orden: "+food+", Precio: $"+price;
                registros.add(orderInfo);
            } while (fila.moveToNext());
        }
        // Creamos el adaptador de ArrayList indicando el contexto (this), el diseño y el origen de los datos
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, registros);
        // Asignamos el adaptador en el ListView
        orderList.setAdapter(adapter);

        db.close(); // cerramos la conexión
    }

    public void updateOrder(View view){
        // Conexión de SQLite con la base de datos creada
        AdminSQLite admin = new AdminSQLite(this, "MCDONNALS", null, 1);
        // Obtenemos los datos de la Conexión
        SQLiteDatabase db = admin.getWritableDatabase();

        // Validamos que los campos NO esten vacíos
        if(txtClientName.getText().toString().isEmpty()
           && txtFoodName.getText().toString().isEmpty()
           && txtOrderPrice.getText().toString().isEmpty()
           && txtOrderID.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Rellene los campos", Toast.LENGTH_SHORT).show();
        } else {
            // Extraemos las variables del formulario
            String client = txtClientName.getText().toString();
            String food = txtFoodName.getText().toString();
            Double price = Double.parseDouble(txtOrderPrice.getText().toString());
            String id = txtOrderID.getText().toString();

            // Array de datos que almacenará los datos para realizar el INSERT
            ContentValues orderData = new ContentValues();

            // Insertamos los datos en el Array de datos creado
            orderData.put("NOMBRE_CLIENTE", client);
            orderData.put("COMIDA", food);
            orderData.put("PRECIO", price);

            int cantidadUpdateada = db.update("MCDONNALS", orderData, "ID_PEDIDO = "+id, null);

            if(cantidadUpdateada == 1){
                Toast.makeText(this, "Pedido actualizado éxitosamente", Toast.LENGTH_SHORT).show();

                // Limpiamos los campos
                txtClientName.setText("");
                txtFoodName.setText("");
                txtOrderPrice.setText("");
                txtOrderID.setText("");

                // Volvemos a habilitar el botón para guardar pedidos
                btnSaveOrder.setEnabled(true);

                // Deshabilitamos los botones para ACTUALIZAR y ELIMINAR
                btnUpdateOrder.setEnabled(false);
                btnDeleteOrder.setEnabled(false);

                seeOrders(); // Actualiza la lista de pedidos
            } else {
                Toast.makeText(this, "No se actualizó el pedido", Toast.LENGTH_SHORT).show();

                // Limpiamos los campos
                txtClientName.setText("");
                txtFoodName.setText("");
                txtOrderPrice.setText("");
                txtOrderID.setText("");

                // Volvemos a habilitar el botón para guardar pedidos
                btnSaveOrder.setEnabled(true);

                // Deshabilitamos los botones para ACTUALIZAR y ELIMINAR
                btnUpdateOrder.setEnabled(false);
                btnDeleteOrder.setEnabled(false);
            }
        }
        db.close(); // Cerramos la conexión
    }

    public void deleteOrder(View view){
        // Conexión de SQLite con la base de datos creada
        AdminSQLite admin = new AdminSQLite(this, "MCDONNALS", null, 1);
        // Obtenemos los datos de la Conexión
        SQLiteDatabase db = admin.getWritableDatabase();

        // Validamos que los campos NO esten vacíos
        if(txtOrderID.getText().toString().isEmpty()) {
            Toast.makeText(this, "Seleccione un elemento a eliminar", Toast.LENGTH_SHORT).show();
        } else {
            // Extraemos las variables del formulario
            String id = txtOrderID.getText().toString();

            int cantidadDeleteada = db.delete("MCDONNALS", "ID_PEDIDO = "+id, null);

            if(cantidadDeleteada == 1){
                Toast.makeText(this, "Pedido eliminado éxitosamente", Toast.LENGTH_SHORT).show();

                // Limpiamos los campos
                txtClientName.setText("");
                txtFoodName.setText("");
                txtOrderPrice.setText("");
                txtOrderID.setText("");

                // Volvemos a habilitar el botón para guardar pedidos
                btnSaveOrder.setEnabled(true);

                // Deshabilitamos los botones para ACTUALIZAR y ELIMINAR
                btnUpdateOrder.setEnabled(false);
                btnDeleteOrder.setEnabled(false);

                seeOrders(); // Actualiza la lista de pedidos
            } else {
                Toast.makeText(this, "No se eliminó el pedido", Toast.LENGTH_SHORT).show();

                // Limpiamos los campos
                txtClientName.setText("");
                txtFoodName.setText("");
                txtOrderPrice.setText("");
                txtOrderID.setText("");

                // Volvemos a habilitar el botón para guardar pedidos
                btnSaveOrder.setEnabled(true);

                // Deshabilitamos los botones para ACTUALIZAR y ELIMINAR
                btnUpdateOrder.setEnabled(false);
                btnDeleteOrder.setEnabled(false);
            }
        }
        db.close(); // Cerramos la conexión
    }

}
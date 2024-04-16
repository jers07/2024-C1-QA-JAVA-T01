package com.sofkau.integration.database.mysql;

import com.sofkau.integration.database.ClasesDB.Publicaciones;
import com.sofkau.integration.database.DataBase;;
import java.sql.*;

import static com.sofkau.integration.database.mysql.MySqlConstants.CONNECTION_STRING;
import static com.sofkau.integration.database.mysql.MySqlConstants.MY_SQL_JDBC_DRIVER;

public class MySqlOperation implements DataBase {

    Publicaciones publicacion = new Publicaciones();

    private Connection connection= null;
    private Statement statement=null;
    private ResultSet resultSet=null;

    private String sqlStatement;
    private String server = "localhost";
    private String dataBaseName = "laPinguinera";
    private String user = "root";
    private String password = "123456";

    public String getSqlStatement() {
        return sqlStatement;
    }

    public void setSqlStatement(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public ResultSet getResulset() {
        return resultSet;
    }

    @Override
    public void configureDataBaseConnection() {
        try {
            Class.forName(MY_SQL_JDBC_DRIVER);
            connection= DriverManager.getConnection(
                    String.format(CONNECTION_STRING,
                            this.server,
                            this.dataBaseName,
                            this.user,
                            this.password)
            );
            statement=connection.createStatement();

        }catch (Exception e){
            close();
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void executeSqlStatement() {

        try {
            configureDataBaseConnection();
            resultSet = statement.executeQuery(sqlStatement);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public void executeSqlStatementWithParameters(Object... parameters) {
        try {
            configureDataBaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);

            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void executeSqlStatementVoid() {
        try {
            configureDataBaseConnection();
            statement.execute(sqlStatement);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }



    @Override
    public ResultSet executeSqlStatementReturnResultSet() throws SQLException {

        ResultSet resultSet = null;
        try {
            configureDataBaseConnection();
            resultSet = statement.executeQuery(sqlStatement);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return resultSet;
    }


    @Override
    public void close() {
        try {
            if(resultSet !=null){
                resultSet.close();
            }
            if(statement !=null){
                statement.close();
            }
            if(connection !=null){
                connection.close();
            }

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void printResulset() throws SQLException {

        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int totalColumnNumber = resultSetMetaData.getColumnCount();
        while (resultSet.next()) {
            for (int columnNumber = 1; columnNumber <= totalColumnNumber; columnNumber++) {
                if (columnNumber > 1) {
                    System.out.print(",\t");
                }
                String columnValue = resultSet.getString(columnNumber);
                System.out.print(resultSetMetaData.getColumnName(columnNumber) + ": " + columnValue);
            }
            System.out.println("");
        }

    }
    public ResultSet executeSqlStatementWithParametersConsult(Object... parameters) {
        try {
            configureDataBaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);

            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }

            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return resultSet;
    }

    public ResultSet executeSqlStatementWithParametersDisponibilidad(Object... parameters) {
        try {
            configureDataBaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);

            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {

                    String cantidadDisponibleStr = resultSet.getString("cantidadDisponible");
                    String cantidadPrestadoStr = resultSet.getString("cantidadPrestado");



                    int cantidadDisponible = Integer.parseInt(cantidadDisponibleStr);
                    int cantidadPrestado = Integer.parseInt(cantidadPrestadoStr);

                    publicacion.setCantidadDisponible(cantidadDisponible);
                    publicacion.setCantidadPrestado(cantidadPrestado);

                } else {
                    System.out.println("No se encontró ninguna publicación con el título especificado.");
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return resultSet;
    }

}
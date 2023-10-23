package com.laptrinhjavaweb.dao.impl;
import com.laptrinhjavaweb.dao.GenericDAO;
import com.laptrinhjavaweb.mapper.RowMapper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AbstractDAO<T> implements GenericDAO<T> {
  ResourceBundle resourceBundle = ResourceBundle.getBundle("db");
  public Connection getConnection(){
    try {
//      Class.forName("com.mysql.jdbc.Driver");
//      String url ="jdbc:mysql://localhost:3306/newservlet12month2018";
//      String username="root";
//      String password="Huy572003@.com";

      Class.forName(resourceBundle.getString("driverName"));
      String url =resourceBundle.getString("url");
      String username=resourceBundle.getString("user");
      String password=resourceBundle.getString("password");

      return DriverManager.getConnection(url,username,password);
    } catch (ClassNotFoundException | SQLException e) {
      return null;
    }

  }
  @Override
  public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... parameters) {
    List<T> results = new ArrayList<>();
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try {
      connection = getConnection();
      statement = connection.prepareStatement(sql);
      setParameter(statement, parameters);
      resultSet = statement.executeQuery();
      while (resultSet.next()) {
        results.add(rowMapper.mapRow(resultSet));
      }
      return results;
    } catch (SQLException e) {
      return null;
    } finally {
      try {
        if (connection != null) {
          connection.close();
        }
        if (statement != null) {
          statement.close();
        }
        if (resultSet != null) {
          resultSet.close();
        }
      } catch (SQLException e) {
        return null;
      }
    }
  }

  @Override
  public void update(String sql, Object... parameters) {
    PreparedStatement statement=null;
    Connection connection = null;
    try {

      connection = getConnection();
      connection.setAutoCommit(false);
      statement = connection.prepareStatement(sql);
      setParameter(statement,parameters);
      statement.executeUpdate();
      connection.commit();

    }catch (SQLException e){
      try {
        if (connection != null) {
          connection.rollback();

        }
      } catch (SQLException ex) {
        ex.printStackTrace();
      }finally {
        try {
          if (connection != null) {
            connection.close();
          }
          if (statement != null) {
            statement.close();
          }

        } catch (SQLException ex) {

          ex.printStackTrace();
        }
      }

    }

  }

  @Override
  public Long insert(String sql, Object... parameters) {
    ResultSet resultSet = null;
    PreparedStatement statement = null;
    Connection connection = null;
    try {

      Long id=null;

      connection = getConnection();
      connection.setAutoCommit(false);
      statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
      setParameter(statement, parameters);
      statement.executeUpdate();
      resultSet = statement.getGeneratedKeys();
      if (resultSet.next()) {
        id = resultSet.getLong(1);
      }
      connection.commit();
      return id;

    } catch (SQLException e) {
      try {
        if (connection != null) {
          connection.rollback();

        }
      } catch (SQLException ex) {
        ex.printStackTrace();
      } finally {
        try {
          if (connection != null) {
            connection.close();
          }
          if (statement != null) {
            statement.close();
          }
          if(resultSet!=null)
          {
            resultSet.close();
          }
        } catch (SQLException ex) {

          ex.printStackTrace();
        }
      }

    }
return null;
  }

  @Override
  public int count(String sql, Object... parameters) {

    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try {
      int count=0;

      connection = getConnection();
      statement = connection.prepareStatement(sql);
      setParameter(statement, parameters);
      resultSet = statement.executeQuery();
      while (resultSet.next()) {
        count=resultSet.getInt(1);
      }
      return count;
    } catch (SQLException e) {
      return 0;
    } finally {
      try {
        if (connection != null) {
          connection.close();
        }
        if (statement != null) {
          statement.close();
        }
        if (resultSet != null) {
          resultSet.close();
        }
      } catch (SQLException e) {
        return 0;
      }
    }
  }

  private void setParameter(PreparedStatement statement, Object... parameters) {

    try {
      for(int i=0;i<parameters.length;i++){
        Object parameter= parameters[i];
        int index=i+1;

        if(parameter instanceof Long){
          statement.setLong(index,(Long)parameter);
        } else if (parameter instanceof String) {
          statement.setString(index,(String)parameter);
        } else if (parameter instanceof Integer) {
          statement.setInt(index,(Integer) parameter);
        }else if (parameter instanceof Timestamp) {
          statement.setTimestamp(index, (Timestamp) parameter);
        }
      }


    } catch (SQLException e) {
      e.printStackTrace();

    }


  }




}

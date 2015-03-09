/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
        
/**
 * REST Web Service
 *
 * @author Guillermo
 */
@Path("service/{id:.*}")
public class Films {

    @Context
    private UriInfo context;
//    public InitialContext ctx;
//    public DataSource ds;
//    public Connection conn;
//    public Statement sqlStatement;
    
    /**
     * Creates a new instance of Films
     */
    public Films() throws NamingException, SQLException{

        
    }

    /**
     * Retrieves representation of an instance of service.Films
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@PathParam("id") String filmId) throws JSONException, SQLException, NamingException {
    
        InitialContext ctx= new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("jdbc/conexionPool");
        Connection conn = ds.getConnection();
        Statement sqlStatement = conn.createStatement();
        sqlStatement.execute("USE sakila;");
        
        String sql = String.format("SELECT * FROM film WHERE film_id = '%s'",
                filmId);
        ResultSet res = sqlStatement.executeQuery(sql);
        JSONArray filmsArray = new JSONArray();
        System.out.println("hola");
        while(res.next()){
               JSONObject film = new JSONObject();
                film.put("id",res.getInt( "film_id" ));
                film.put("title",res.getString( "title" ));
                String sqlActors = String.format("SELECT * FROM film_actor WHERE film_id = '%s'",
                        res.getString( "film_id" ));
                ResultSet resActors = conn.createStatement().executeQuery(sqlActors);
                JSONArray actorsArray = new JSONArray();
                while(resActors.next()){
                    String sqlActorData = String.format("SELECT * FROM actor_info WHERE actor_id = '%s'",
                        resActors.getInt( "actor_id" ));
                    ResultSet resActorData = conn.createStatement().executeQuery(sqlActorData);
                    while(resActorData.next()){
                        JSONObject actor = new JSONObject();
                        actor.put("first_name", resActorData.getString( "first_name" ));
                        actor.put("last_name", resActorData.getString( "last_name" ));
                        actorsArray.put(actor);
                    };
                    resActorData.close();
                    
                };
                resActors.close();
                film.put("actors",actorsArray);
                filmsArray.put(film);
        };
         res.close();

        return filmsArray.toString();
    }

    /**
     * PUT method for updating or creating an instance of Films
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}

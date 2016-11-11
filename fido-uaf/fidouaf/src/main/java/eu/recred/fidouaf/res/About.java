package eu.recred.fidouaf.res;

/**
 * Created by georgeg on 13.04.2016.
 */
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.Manifest;

@Path("/about")
public class About {

    @Context
    private ServletContext context;

    @GET
    @Path("/")
    public Response getMsg() {

        Response response = Response.status(200).entity("OK!").build();

        try {
            InputStream inputStream = context.getResourceAsStream("/META-INF/MANIFEST.MF");
            Manifest manifest0 = new Manifest(inputStream);

            StringBuilder sb = new StringBuilder();
            for (Map.Entry entry: manifest0.getMainAttributes().entrySet()) {
                sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            response = Response.status(200).entity(sb.toString()).build();
//
// ap.Entry entry: manifest0.getMainAttributes().entrySet()) {
//                System.out.println(entry.getKey() + ": " + entry.getValue());
//            }
//            System.out.println("---------------------------------------------");

//            Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
//            while (resources.hasMoreElements()) {
//                Manifest manifest = new Manifest(resources.nextElement().openStream());
//                for (Map.Entry entry: manifest.getMainAttributes().entrySet()) {
//                    System.out.println(entry.getKey() + ": " + entry.getValue());
//                }
//                System.out.println("---------------------------------------------");
//
//                response = Response.status(200).entity(manifest.getMainAttributes()).build();
//                //break;
//                //map.put("Implementation-Vendor", manifest.getMainAttributes().getValue("Implementation-Vendor"));
//
//            }
        }catch (IOException ex) {
            ex.printStackTrace();
        }

        return response;
    }
}

package servlets;

import beans.UserProfile;
import dao.DAOFactory;
import dao.DAOUser;
import forms.LoginForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet (name = "servlets.AuthServlet")
public class AuthServlet extends HttpServlet {
    public static final String VUE_LOGIN = "/WEB-INF/jsp/login.jsp";
    //public static final String VUE_ACCUEIL = "/WEB-INF/jsp/threads.jsp";
    private static final String CONF_DAO_FACTORY = "daofactory";
    private static final String SESSION_USER = "sessionUtilisateur";
    private DAOUser daoUser;


    /**
     * Actions à effectuer 1 seule fois à la création de cette servlet
     * @throws ServletException si il y a un problème avec ces actions
     */
    public void init() throws ServletException {
        /* Récupération d'une instance de notre DAO Utilisateur (dans init pour éviter qu'une instance ne soit
         * créée à chaque requête reçue. */
        this.daoUser = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getUtilisateurDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        this.getServletContext().getRequestDispatcher(VUE_LOGIN).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LoginForm form = new LoginForm(daoUser);

        // On récupère l'utilisateur qui a fait la requête de connexion
        UserProfile utilisateur = form.connecterUtilisateur(request);

        // Créer ou récupérer la session existante
        HttpSession session = request.getSession();

        if ( form.getErreurs().isEmpty() ) {
            // Je met le bean utilisateur qui vient de se connecter dans la session
            session.setAttribute( SESSION_USER, utilisateur );
        } else {
            // On ne met rien dan la session car il y a eu des erreurs
            session.setAttribute( SESSION_USER, null );
        }

        /* Stockage du formulaire et du bean dans l'objet request */
        request.setAttribute( "form", form );
        request.setAttribute( "utilisateur", utilisateur );

        this.getServletContext().getRequestDispatcher( VUE_LOGIN ).forward( request, response );
    }
}

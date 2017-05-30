package servlet;

import entity.Users;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 11981 on 2017/5/30.
 */
public class RegServlet extends HttpServlet{
    public RegServlet(){
        super();
    }

    public void destroy(){
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException{
        doPost(request,response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException{
        request.setCharacterEncoding("utf-8");

        Users u = new Users();
        String username, mypassword, gender, email, introduce,isAccept;
        Date birthday;
        String[] favorites;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try{
            username = request.getParameter("username");
            mypassword = request.getParameter("mypassword");
            gender = request.getParameter("gender");
            email = request.getParameter("email");
            introduce = request.getParameter("introduce");
            birthday = sdf.parse(request.getParameter("birthday"));
            if (request.getParameterValues("isAccept") != null){
                isAccept = request.getParameter("isAccept");
            }else{
                isAccept = "false";
            }

            favorites = request.getParameterValues("favorite");

            u.setUsername(username);
            u.setMypassword(mypassword);
            u.setGender(gender);
            u.setEmail(email);
            u.setFavorites(favorites);
            u.setIntroduces(introduce);
            if (isAccept.equals("true")){
                u.setFlag(true);
            }else{
                u.setFlag(false);
            }
            u.setBirthday(birthday);

            request.getSession().setAttribute("regUser", u);
            request.getRequestDispatcher("../userinfo.jsp").forward(request, response);
        }catch (Exception ex){
            ex.printStackTrace();

        }
    }

    public void init() throws ServletException{

    }
}

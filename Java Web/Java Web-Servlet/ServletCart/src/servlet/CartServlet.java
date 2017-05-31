package servlet;

import dao.ItemsDAO;
import entity.Cart;
import entity.Items;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by 11981 on 2017/5/30.
 */
public class CartServlet extends HttpServlet {

    private String action;//定义购物车动作,add,show,delete

    private ItemsDAO idao = new ItemsDAO();

    public CartServlet(){
        super();
    }

    @Override
    public void destroy(){

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //设置格式
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        if (request.getParameter("action") != null){
            this.action = request.getParameter("action");
            if (action.equals("add"))
            {
                if (addToCart(request, response))
                {
                    request.getRequestDispatcher("/success.jsp").forward(request, response);
                }else{
                    request.getRequestDispatcher("/failure.jsp").forward(request, response);
                }
            }
            if (action.equals("show")){
                request.getRequestDispatcher("/cart.jsp").forward(request, response);
            }

            if (action.equals("delete")){
                if (deleteFormCart(request, response))
                {
                    request.getRequestDispatcher("/cart.jsp").forward(request, response);
                }else
                {
                    request.getRequestDispatcher("/cart.jsp").forward(request, response);
                }
            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doPost(request,response);
    }

    //添加商品进购物车
    private boolean addToCart(HttpServletRequest request, HttpServletResponse response)
    {
        String id = request.getParameter("id");
        String number = request.getParameter("num");
        Items item = idao.getItemsById(Integer.parseInt(id));

        if (request.getSession().getAttribute("cart") == null)
        {
            Cart cart = new Cart();
            request.getSession().setAttribute("cart", cart);
        }
        Cart cart = (Cart)request.getSession().getAttribute("cart");
        if (cart.addGoodsInCart(item, Integer.parseInt(number)))
        {
            return true;
        }else{
            return false;
        }
    }

    private boolean deleteFormCart(HttpServletRequest request, HttpServletResponse response)
    {
        String id = request.getParameter("id");
        Cart cart = (Cart)request.getSession().getAttribute("cart");
        Items item = idao.getItemsById(Integer.parseInt(id));
        if (cart.removeGoodsFromCart(item))
        {
            return true;
        }else {
            return false;
        }
    }

    public void init() throws ServletException{

    }
}

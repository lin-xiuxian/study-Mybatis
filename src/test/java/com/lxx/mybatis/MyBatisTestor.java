package com.lxx.mybatis;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lxx.mybatis.dto.GoodsDTO;
import com.lxx.mybatis.entity.Goods;
import com.lxx.mybatis.entity.GoodsDetail;
import com.lxx.mybatis.utils.MybatisUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.*;

public class MyBatisTestor {
    /**
     * 初始化SqlSessionFactory
     * @throws IOException
     */
    @Test
    public void testSqlSessionFactory() throws IOException {
        //利用Reader加载classpath下的mybatis-config.xml核心配置文件
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        //初始化SqlSessionFactory对象,同时解析mybatis-config.xml文件
        //SqlSessionFactory 对象用于构建 SqlSession 对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        System.out.println("SessionFactory success!");
        SqlSession sqlSession = null;
        try {
            //创建SqlSession对象,SqlSession是JDBC的扩展类,用于与数据库交互
            sqlSession = sqlSessionFactory.openSession();
            //创建数据库连接(测试用)
            Connection connection = sqlSession.getConnection();
            System.out.println(connection);
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            if(sqlSession != null){
                //如果type="POOLED",代表使用连接池,close则是将连接回收到连接池中
                //如果type="UNPOOLED",代表直连,close则会调用Connection.close()方法关闭连接
                sqlSession.close();
            }
        }
    }

    @Test
    public void testMybatisUtils(){
        SqlSession sqlSession = null;
        try {
            sqlSession = MybatisUtils.openSession();
            Connection connection = sqlSession.getConnection();
            System.out.println(connection);
        } catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(sqlSession);
        }
    }
    @Test
    public void testSelectAll(){
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            List<Goods> list =  session.selectList("goods.selectAll");
            for(Goods g: list){
                System.out.println(g.getTitle());
            }
        } catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectById() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            Goods goods = session.selectOne("goods.selectById", 1603);
            System.out.println(goods.getTitle());
        }catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectByPriceRange() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            Map<String, Integer> param = new HashMap<>();
            param.put("min", 100);
            param.put("max", 500);
            param.put("limit", 10);
            List<Goods> list = session.selectList("selectByPriceRange", param);
            for(Goods g: list){
                System.out.println(g.getTitle() + ":" + g.getCurrentPrice());
            }
        }catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectGoodsMap() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            List<Map> list = session.selectList("goods.selectGoodsMap");
            for(Map map: list){
                System.out.println(map);
            }
        } catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectGoodsDTO(){
         SqlSession session = null;
         try{
             session = MybatisUtils.openSession();
             List<GoodsDTO> list = session.selectList("goods.selectGoodsDTO");
             for(GoodsDTO g: list){
                 System.out.println(g.getGoods().getTitle());
             }
         } catch(Exception e){
             throw e;
         } finally{
             MybatisUtils.closeSession(session);
         }
    }

    @Test
    public void testInsert() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            Goods goods = new Goods();
            goods.setTitle("测试商品");
            goods.setSubTitle("测试子标题");
            goods.setOriginalCost(200f);
            goods.setCurrentPrice(100f);
            goods.setDiscount(0.5f);
            goods.setIsFreeDelivery(1);
            goods.setCategoryId(43);
            //insert 返回值代表成功插入的记录总数
            int num = session.insert("goods.insert", goods);
            session.commit();
            System.out.println(goods.getGoodsId());
        } catch(Exception e){
            if(session != null){
                session.rollback();
            }
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testUpdate() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            Goods goods  = session.selectOne("goods.selectById", 739);
            goods.setTitle("更新测试商品");
            int num = session.update("goods.update", goods);
            session.commit();
        } catch(Exception e){
            if(session != null){
                session.rollback();
            }
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testDelete() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            int num = session.delete("goods.delete", 739);
            session.commit();
        } catch(Exception e){
            if(session != null){
                session.rollback();
            }
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectByTitle() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            Map param = new HashMap();
            /*
                ${}原文传值
                select * from t_goods
                where title = '' or 1 =1 or title = '【德国】爱他美婴幼儿配方奶粉1段800g*2罐 铂金版'
            */
            /*
               #{}预编译
               select * from t_goods
                where title = "'' or 1 =1 or title = '【德国】爱他美婴幼儿配方奶粉1段800g*2罐 铂金版'"
            */
            param.put("title", "【德国】爱他美婴幼儿配方奶粉pre段800g*2罐 铂金版");
            param.put("order", "title desc");
            List<Goods> list = session.selectList("goods.selectByTitle", param);
            for(Goods g:list){
                System.out.println(g.getTitle() + ":" + g.getCurrentPrice());
            }
        } catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testDynamicSQL() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            Map param = new HashMap();
            param.put("categoryId", 44);
            param.put("currentPrice", 500);
            List<Goods> list = session.selectList("goods.dynamicSQL", param);
            for(Goods g: list){
                System.out.println(g.getTitle() + ":" + g.getCurrentPrice());
            }
        } catch(Exception e){
            throw e;
        } finally {
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testLv1Cache() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            Goods goods = session.selectOne("goods.selectById", 1603);
            Goods goods1 = session.selectOne("goods.selectById", 1603);
            System.out.println(goods.hashCode() + ":" + goods1.hashCode());
        }catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }

        try{
            session = MybatisUtils.openSession();
            Goods goods = session.selectOne("goods.selectById", 1603);
            session.commit();
            Goods goods1 = session.selectOne("goods.selectById", 1603);
            System.out.println(goods.hashCode() + ":" + goods1.hashCode());
        }catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testLv2Cache() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            Goods goods = session.selectOne("goods.selectById", 1603);
            System.out.println(goods.hashCode());
        }catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }

        try{
            session = MybatisUtils.openSession();
            Goods goods = session.selectOne("goods.selectById", 1603);
            System.out.println(goods.hashCode());
        }catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }
    /**
     *  一对多对象关联查询
     *  @throw Exception
     */
    @Test
    public void testOneToMany() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            List<Goods> list = session.selectList("goods.selectOneToMany");
            for(Goods g:list){
                System.out.println(g.getTitle() + ":" + g.getGoodsDetails().size());
            }
        } catch(Exception e){
            if(session != null){
                System.out.println("失败");
            }
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }
    /**
     * 测试多对一对象关联映射
     * */
    @Test
    public void testManyToOne() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            List<GoodsDetail> list = session.selectList("goodsDetail.selectManyToOne");
            for(GoodsDetail gd: list){
                System.out.println(gd.getGdPicUrl() + ":" + gd.getGoods().getTitle());
            }
        } catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testSelectPage() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            PageHelper.startPage(2,10);
            Page page = (Page) session.selectList("goods.selectPage");
            System.out.println("总页数: " + page.getPages());
            System.out.println("总记录数: " + page.getTotal());
            System.out.println("开始行号: " + page.getStartRow());
            System.out.println("结束行号: " + page.getEndRow());
            System.out.println("当前页码: " + page.getPageNum());
            List<Goods> data = page.getResult(); //当前页数据
            for(Goods g: data){
                System.out.println(g.getTitle());
            }
        } catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testBatchInsert() throws Exception{
        SqlSession session = null;
        try{
            long st = new Date().getTime();
            session = MybatisUtils.openSession();
            List list = new ArrayList();
            for(int i = 0; i < 500; i++){
                Goods goods = new Goods();
                goods.setTitle("测试商品");
                goods.setSubTitle("测试子标题");
                goods.setOriginalCost(200f);
                goods.setCurrentPrice(100f);
                goods.setDiscount(0.5f);
                goods.setIsFreeDelivery(1);
                goods.setCategoryId(43);

                list.add(goods);
            }
            session.insert("goods.batchInsert", list);
            session.commit();
            long et = new Date().getTime();
            System.out.println("执行时间: " + (et - st) + " 毫秒");
        } catch(Exception e){
            if(session != null){
                session.rollback();
            }
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }

    @Test
    public void testBatchDelete() throws Exception{
        SqlSession session = null;
        try{
            long st = new Date().getTime();
            session = MybatisUtils.openSession();
            List list = new ArrayList();
            for(int i = 2674; i <= 3181; i++){
                list.add(i);
            }
            session.delete("goods.batchDelete", list);
            session.commit();
            long et = new Date().getTime();
            System.out.println("执行时间: " + (et - st) + " 毫秒");
        } catch(Exception e){
            if(session != null){
                session.rollback();
            }
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }
}

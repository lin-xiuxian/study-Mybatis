package com.lxx.mybatis;

import com.lxx.mybatis.dao.GoodsDAO;
import com.lxx.mybatis.dto.GoodsDTO;
import com.lxx.mybatis.entity.Goods;
import com.lxx.mybatis.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class MyBatisTestor {
    @Test
    public void testSelectByPriceRange(){
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            GoodsDAO goodsDAO = session.getMapper(GoodsDAO.class);
            List<Goods> list = goodsDAO.selectByPriceRange(100f, 500f, 20);
            System.out.println(list.size());
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
            GoodsDAO goodsDAO = session.getMapper(GoodsDAO.class);
            int num = goodsDAO.insert(goods);
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
    public void testSelectAll() throws Exception{
        SqlSession session = null;
        try{
            session = MybatisUtils.openSession();
            GoodsDAO goodsDAO = session.getMapper(GoodsDAO.class);
            List<GoodsDTO> list = goodsDAO.selectAll();
            System.out.println(list.size());
        } catch(Exception e){
            throw e;
        } finally{
            MybatisUtils.closeSession(session);
        }
    }
}

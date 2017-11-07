package com.qingwing.safebox.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wangshengyin on 2017-04-26.
 * email:shoxgov@126.com
 */

public class SortList<E> {

    public static SortList sortList;

    public SortList() {

    }

    public static SortList getInstance() {
        if (null == sortList) {
            sortList = new SortList();
        }
        return sortList;
    }


    public void Sort(List<E> list, final String method, final String sort) {
        Collections.sort(list, new Comparator() {
            public int compare(Object a, Object b) {
                int ret = 0;
                try {
                    Method m1 = ((E) a).getClass().getMethod(method, new Class[0]);
                    Method m2 = ((E) b).getClass().getMethod(method, new Class[0]);
                    if (sort != null && "desc".equals(sort))//倒序
                        ret = m2.invoke(((E) b), new Object[]{}).toString().compareTo(m1.invoke(((E) a), new Object[]{}).toString());
                    else//正序
                        ret = m1.invoke(((E) a), new Object[]{}).toString().compareTo(m2.invoke(((E) b), new Object[]{}).toString());
                } catch (NoSuchMethodException ne) {
                    System.out.println(ne);
                } catch (IllegalAccessException ie) {
                    System.out.println(ie);
                } catch (InvocationTargetException it) {
                    System.out.println(it);
                }
                return ret;
            }
        });
    }

    /**
     * 此方法获取列表的前六条数据
     */
    public List<E> getSixList(List<E> list) {
        List<E> list1 = new ArrayList<E>();
        list1.add(list.get(0));
        list1.add(list.get(1));
        list1.add(list.get(2));
        list1.add(list.get(3));
        list1.add(list.get(4));
        list1.add(list.get(5));
        return list1;
    }

}

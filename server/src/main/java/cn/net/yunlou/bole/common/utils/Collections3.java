/*
 *
 *     Copyright (c) 2019 - forever, javaeer All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the haoheio.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * Author: javaeer (javaeer@aliyun.com)
 *
 */
package cn.net.yunlou.bole.common.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Collections工具集.
 * 在JDK的Collections和Guava的Collections2后, 命名为Collections3.
 *
 * @author calvin
 * @version 2013-01-15
 * @updater Macheal
 */
@SuppressWarnings("rawtypes")
public class Collections3 {

    /**
     * 转换Collection所有元素(通过toString())为String,
     * 中间以 separator分隔。
     */
    public static String convertToString(final Collection collection, final String separator) {
        return StringUtils.join(collection, separator);
    }

    /**
     * 转换Collection所有元素(通过toString())为String,
     * 每个元素的前面加入prefix，后面加入postfix，如<div>mymessage</div>。
     */
    public static String convertToString(final Collection collection, final String prefix, final String postfix) {
        StringBuilder builder = new StringBuilder();
        for (Object o : collection) {
            builder.append(prefix).append(o).append(postfix);
        }
        return builder.toString();
    }


    /**
     * @param t
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(T t) {
        return ObjectUtils.isEmpty(t);
    }


    /**
     * 数组是否为非空<br>
     * 此方法会匹配单一对象，如果此对象为{@code null}则返回false<br>
     * 如果此对象为非数组，理解为此对象为数组的第一个元素，则返回true<br>
     * 如果此对象为数组对象，数组长度大于0情况下返回true，否则返回false
     *
     * @param t 数组
     * @return 是否为非空
     */
    public static <T> boolean isNotEmpty(T t) {
        return false == isEmpty(t);
    }

    /**
     * 所有对象 是否都 为{@code null}或空，通过{@link ObjectUtils#isEmpty(Object)} 判断元素
     *
     * @param ts 被检查的对象,一个或者多个
     * @return 是否都为空
     */
    public static <T> boolean isAllEmpty(T... ts) {
        return emptyCount(ts) == ts.length;
    }

    /**
     * 所有对象 是否都 不为{@code null ""}
     * 通过{@link ObjectUtils#isEmpty(Object)} 判断元素
     *
     * @param ts 被检查的对象,一个或者多个
     * @return 是否都不为空
     */
    public static <T> boolean isAllNotEmpty(T... ts) {
        return !hasEmpty(ts);
    }

    /**
     * 是否存在 不为{@code null}或空对象，通过{@link ObjectUtils#isEmpty(Object)} 判断元素
     *
     * @param ts  被检查的对象,一个或者多个
     * @param <T>
     * @return 是否不都为空
     */
    public static <T> boolean isNotAllEmpty(T... ts) {
        //return !isAllEmpty(ts);
        return hasNotEmpty(ts);
    }


    /**
     * 是否包含{@code null "" }元素
     *
     * @param <T> 数组元素类型
     * @param ts  被检查的数组
     * @return 是否包含{@code null ""}元素
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean hasEmpty(T... ts) {
        if (ObjectUtils.isNotEmpty(ts)) {
            for (T element : ts) {
                if (ObjectUtils.isEmpty(element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 计算{@code null}或空元素对象的个数，通过{@link ObjectUtils#isEmpty(Object)} 判断元素
     *
     * @param ts 被检查的对象,一个或者多个
     * @return 存在{@code null ""}的数量
     * @since 4.5.18
     */
    public static <T> int emptyCount(T... ts) {
        int count = 0;
        if (isNotEmpty(ts)) {
            for (Object element : ts) {
                if (ObjectUtils.isEmpty(element)) {
                    count++;
                }
            }
        }
        return count;
    }


    /**
     * 是否包含不为 {@code null "" }元素
     *
     * @param <T> 数组元素类型
     * @param ts  被检查的数组
     * @return 是否包含不为{@code null ""}元素
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean hasNotEmpty(T... ts) {
        if (ObjectUtils.isNotEmpty(ts)) {
            for (T element : ts) {
                if (ObjectUtils.isNotEmpty(element)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 取得Collection的第一个元素，如果collection为空返回null.
     */
    public static <T> T getFirst(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }

        return collection.iterator().next();
    }

    /**
     * 获取Collection的最后一个元素 ，如果collection为空返回null.
     */
    public static <T> T getLast(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }

        //当类型为List时，直接取得最后一个元素 。
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }

        //其他类型通过iterator滚动到最后一个元素.
        Iterator<T> iterator = collection.iterator();
        while (true) {
            T current = iterator.next();
            if (!iterator.hasNext()) {
                return current;
            }
        }
    }

    /**
     * 返回a与b的并集新List.
     */
    public static <T> List<T> union(final Collection<T> a, final Collection<T> b) {
        List<T> result = new ArrayList<T>(a);
        result.addAll(b);
        return result;
    }

    /**
     * 返回a与b差集的新List.
     */
    public static <T> List<T> subtract(final Collection<T> a, final Collection<T> b) {
        List<T> list = new ArrayList<T>(a);
        for (T element : b) {
            list.remove(element);
        }

        return list;
    }

    /**
     * 返回a与b的交集的新List.
     */
    public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
        List<T> list = new ArrayList<T>();

        for (T element : a) {
            if (b.contains(element)) {
                list.add(element);
            }
        }
        return list;
    }


    /**
     * 返回排序后的List
     *
     * @param set
     * @return
     */
    public static List<Map.Entry<String, String>> sortCollection(Set<Map.Entry<String, String>> set) {

        List<Map.Entry<String, String>> list = new LinkedList<Map.Entry<String, String>>(set);
        Collections.sort(list, (o1, o2) -> (o2.getKey()).compareTo(o1.getKey()));
        return list;
    }

    public static <E> Set<E> asSet(E... elements) {
        if (elements == null || elements.length == 0) {
            return Collections.emptySet();
        }

        if (elements.length == 1) {
            return Collections.singleton(elements[0]);
        }

        LinkedHashSet<E> set = new LinkedHashSet<>(elements.length * 4 / 3 + 1);
        Collections.addAll(set, elements);
        return set;
    }

    public static <E> List<E> asList(E... elements) {
        if (elements == null || elements.length == 0) {
            return Collections.emptyList();
        }

        // Integer overflow does not occur when a large array is passed in because the list array already exists
        return Arrays.asList(elements);
    }


    public static int[] toIntArray(List<Integer> list) {
        return list.stream().filter(integer -> integer != null).mapToInt(i -> i).toArray();
    }

    public static String[] toStrArray(List<String> list) {
        return list.stream().filter(str -> StringUtils.isNotBlank(str)).toArray(String[]::new);
    }

    public static Set<?> toSet(List<?> list) {
        return list.stream().collect(Collectors.toSet());
    }

    public static List<?> toList(Set<?> set) {
        return set.stream().collect(Collectors.toList());
    }


    public static void main(String[] args) {
        System.out.println(hasNotEmpty("5", null));
        //System.out.println(hasEmpty("", 2));
    }
}

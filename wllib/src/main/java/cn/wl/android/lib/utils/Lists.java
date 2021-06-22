package cn.wl.android.lib.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import io.reactivex.functions.Consumer;


/**
 * Created by JustBlue on 2019-10-28.
 *
 * @email: bo.li@cdxzhi.com
 * @desc:
 */
public class Lists {

    /**
     * 返回数据与下标
     *
     * @param list
     * @param consumer
     * @param <T>
     */
    public static <T> void forWithIndex(@Nullable Collection<T> list, @NonNull Actions.Filter<DI<T>> consumer) {
        if (isEmpty(list)) return;

        int index = 0;
        for (T data : list) {
            consumer.check(new DI<>(index, data));
            index++;
        }
    }

    /**
     * 通过下标转换
     *
     * @param list
     * @param mapper
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> mapWithIndex(@Nullable Collection<T> list, @NonNull Actions.Mapper<DI<T>, R> mapper) {
        if (isEmpty(list)) return new ArrayList<>(0);

        int index = 0;
        List<R> temp = new ArrayList<>(list.size());

        for (T data : list) {
            R map = mapper.map(new DI<>(index, data));
            temp.add(map);

            index++;
        }

        return temp;
    }

    /**
     * 判断是否存在满足条件的元素, 如存在一个及返回true
     *
     * @param list
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> boolean any(@Nullable Collection<T> list, @NonNull Actions.Filter<T> predicate) {
        if (isEmpty(list)) return false;

        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T next = it.next();

            if (predicate.check(next)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 通过条件查找指定的第一个item, 如没有返回null
     *
     * @param data
     * @param filter
     * @param <T>
     * @return
     */
    public static <T> T getOrNull(Collection<T> data, @NonNull Actions.Filter<T> filter) {
        if (isEmpty(data)) return null;

        Iterator<T> it = data.iterator();
        while (it.hasNext()) {
            T next = it.next();

            if (filter.check(next)) {
                return next;
            }
        }
        return null;
    }

    public static <T> T getOrNullByIndex(List<T> data, int index) {
        if (!isEmpty(data)) {
            if (0 <= index && index < data.size()) {
                return data.get(index);
            }
        }

        return null;
    }

    /**
     * 判断下标
     *
     * @param data
     * @param filter
     * @param <T>
     * @return
     */
    public static <T> int indexOf(List<T> data, @NonNull Actions.Filter<T> filter) {
        if (isEmpty(data)) return -1;

        for (int i = 0; i < data.size(); i++) {
            T t = data.get(i);
            if (filter.check(t)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 循环调用
     *
     * @param list
     * @param forAction
     * @param <T>
     */
    public static <T> void forEach(Collection<T> list, Consumer<T> forAction) {
        if (isEmpty(list)) return;

        for (T t : list) {
            try {
                forAction.accept(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取第一个满足条件item, 否则返回空
     *
     * @param list
     * @param filter
     * @param <T>
     * @return
     */
    public static <T> T firstOrNull(Collection<T> list, Actions.Filter<T> filter) {
        if (isEmpty(list)) return null;

        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T next = it.next();

            if (filter.check(next)) {
                return next;
            }
        }

        return null;
    }

    /**
     * 高精度乘法运算
     *
     * @param v1 乘数
     * @param v2 被乘数
     * @return 两个参数的积
     */
    public static double mulMoney(double v1, double v2, double v3) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        BigDecimal b3 = new BigDecimal(Double.toString(v3));

        try {
            return b1.multiply(b2)
                    .multiply(b3)
                    .doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 拼接字符串
     *
     * @param source
     * @param mapper
     * @param <T>
     * @return
     */
    public static <T> String joinToString(Collection<T> source, Actions.Mapper<T, String> mapper) {
        return joinToString(source, ",", mapper);
    }

    /**
     * 拼接字符串
     *
     * @param source
     * @param mapper
     * @param <T>
     * @return
     */
    public static <T> String joinToString(Collection<T> source, String sp, Actions.Mapper<T, String> mapper) {
        if (isEmpty(source)) return "";

        StringBuffer sb = new StringBuffer();

        Iterator<T> it = source.iterator();

        if (it.hasNext()) {
            T next = it.next();
            sb.append(mapper.map(next));
        }

        while (it.hasNext()) {
            T next = it.next();
            sb.append(sp);
            sb.append(mapper.map(next));
        }

        return sb.toString();
    }

    /**
     * 转换realmList to List
     *
     * @param source
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(Collection<T> source) {
        if (source == null) {
            return new ArrayList<>(0);
        }

        ArrayList<T> list = new ArrayList<>();

        Iterator<T> iterator = source.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            list.add(next);
        }

        return list;
    }

    /**
     * 转换集合
     *
     * @param source
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> ArrayList<R> map(@NonNull Collection<T> source, @NonNull Actions.Mapper<T, R> mapper) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>(0);
        }
        ArrayList<R> temp = new ArrayList<>();

        for (T t : source) {
            temp.add(mapper.map(t));
        }

        return temp;
    }

    /**
     * 转换集合
     *
     * @param source
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> mapV1(@NonNull Collection<T> source, @NonNull Actions.Mapper<T, R> mapper) {
        if (source == null) {
            return new ArrayList<>(0);
        }
        ArrayList<R> temp = new ArrayList<>();

        for (T t : source) {
            temp.add(mapper.map(t));
        }

        return temp;
    }

    /**
     * 判断集合是否为空
     *
     * @param collection
     * @return
     */
    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 过滤列表的数据
     *
     * @param list
     * @param filter
     * @param <T>
     * @return
     */
    public static <T> List<T> filter(Collection<T> list, Actions.Filter<T> filter) {
        if (isEmpty(list)) return new ArrayList<>(0);

        ArrayList<T> objects = new ArrayList<>();
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T next = it.next();
            if (filter.check(next)) {
                objects.add(next);
            }
        }

        return objects;
    }

    /**
     * 添加带筛选的转换
     * (注: {@link cn.wl.android.lib.utils.Actions.Mapper#map(Object)} 返回null,
     * 表示被舍弃, 不会存在返回的列表中)
     *
     * @param collection
     * @param mapper
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> mapFilter(Collection<T> collection, Actions.Mapper<T, R> mapper) {
        if (isEmpty(collection)) return new ArrayList<>(0);

        ArrayList<R> objects = new ArrayList<>();
        Iterator<T> it = collection.iterator();
        while (it.hasNext()) {
            T next = it.next();
            R data = mapper.map(next);

            if (data != null) {
                objects.add(data);
            }
        }

        return objects;
    }

    /**
     * 添加带筛选的转换
     * (注: {@link cn.wl.android.lib.utils.Actions.Mapper#map(Object)} 返回null,
     * 表示被舍弃, 不会存在返回的列表中)
     *
     * @param collection
     * @param mapper
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> mapOnceFilter(Collection<T> collection, Actions.Mapper<T, R> mapper) {
        if (isEmpty(collection)) return new ArrayList<>(0);

        LinkedHashSet<R> objects = new LinkedHashSet<>();
        Iterator<T> it = collection.iterator();
        while (it.hasNext()) {
            T next = it.next();
            R data = mapper.map(next);

            if (data != null) {
                objects.add(data);
            }
        }

        return toList(objects);
    }

    /**
     * 去重
     *
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> List<T> duplicate(Collection<T> collection) {
        LinkedHashSet<T> hashSet = new LinkedHashSet<>();
        Iterator<T> it = collection.iterator();
        while (it.hasNext()) {
            T next = it.next();
            hashSet.add(next);
        }

        return toList(hashSet);
    }

    public static class DI<T> {
        public final int index;
        public final T data;

        public DI(int index, T data) {
            this.index = index;
            this.data = data;
        }
    }

}

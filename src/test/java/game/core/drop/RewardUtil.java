package game.core.drop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RewardUtil {

    private static final Logger logger = LoggerFactory.getLogger(RewardUtil.class);

    /**
     * 物品掉落
     *
     * @param dropIds   掉落物品ID
     * @param countRate 额外百分比
     * @return
     */
    public List<ReturnDropItem> getDropItems(int job, List<Integer> dropIds,
                                             float countRate) {
        if (countRate <= 0) {
            countRate = 1;
        }
        List<ReturnDropItem> list = this.getRandomItems(job, dropIds);
        for (ReturnDropItem rdI : list) {
            rdI.setNum((int) (rdI.getNum() * countRate));
        }
        return list;
    }

    /**
     * 随机得到所有掉落足的物品
     *
     * @param job         职业
     * @param dropListIds
     * @return
     */
    private List<ReturnDropItem> getRandomItems(int job,
                                                List<Integer> dropListIds) {
        List<ReturnDropItem> list = new ArrayList<>();
        try {
            for (Integer dId : dropListIds) {
                pross(dId, job, 1, list);
            }
        } catch (Exception e) {
            // logger.error("从掉落列表中得到物品出错" + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param dropId
     * @param job
     * @param addNum
     * @param list
     * @return
     */
    private List<ReturnDropItem> pross(int dropId, int job, int addNum,
                                       List<ReturnDropItem> list) {
        DropListData data = DropListDataProvider.getInstance().getDatas(dropId);
        if (data != null) {
            List<DropData> dropDatas = toDropData(data);
            int isRepert = data.getIsRepeat();
            int round = data.getRanCount();
            if (dropDatas.size() <= 0) {
                return null;
            } else {
                for (int count = 0; count < round; count++) {
                    int sumRan = 0;// 总权重
                    for (DropData d : dropDatas) {
                        sumRan += d.getRate();
                    }
                    int ran = 0;
                    if (data.getRandomType() == Random_Type.random2.getType()) {
                        ran = getRangedRandom(1, 10000);
                        if (ran > sumRan) {
                            break;
                        }
                    } else if (data.getRandomType() == Random_Type.random1.getType()) {
                        ran = getRangedRandom(1, sumRan);
                    } else {

                    }

                    int sumRate = 0;
                    Iterator<DropData> itr = dropDatas.iterator();
                    while (itr.hasNext()) {
                        DropData d = itr.next();
                        int num = getRangedRandom(d.getMinNum(),
                                d.getMaxNum());
                        if (dropId == d.getItemId()) {
                            logger.error("掉落异常,递归");
                            return null;
                        }
                        sumRate += d.getRate();
                        if (ran <= sumRate) {
                            if (d.getType() == DROP_TYPE.dropId.getType()) {// 就是掉落ID
                                pross(d.getItemId(), job, num, list);
                                if (isRepert == 0) {// 如果是不可重复的
                                    itr.remove();
                                }
                                break;
                            } else {
                                int itemId = this.getItemid(job, d.getItemId());
                                ReturnDropItem di = new ReturnDropItem(itemId,
                                        (int) (num * addNum), d.getType());
                                list.add(di);
                                if (isRepert == 0) {// 如果是不可重复的
                                    itr.remove();// 移除
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    private List<DropData> toDropData(DropListData data) {
        List<DropData> list = new ArrayList<>();
        for (int i = 0; i < data.getItemID().size(); i++) {
            DropData d = new DropData();
            d.setItemId(data.getItemID().get(i));
            d.setType(data.getDropType().get(i));
            d.setRate(data.getDropRate().get(i));
            d.setMinNum(data.getItemNumberMin().get(i));
            d.setMaxNum(data.getItemNumberMax().get(i));
            list.add(d);
        }
        return list;
    }

    /**
     * @param job 如果=-1就没有职业限定
     * @param id
     * @return
     */
    public int getItemid(int job, int id) {
        if (job == -1) {
            return id;
        }
        int T5 = (int) (id / 100000000);
        if (T5 == 1 || T5 == 2) {
            int newId = id - (id - id / 10000000 * 10000000) / 100000 * 100000
                    + job * 100000;
            return newId;
        } else {
            return id;
        }
    }

    /**
     * 返回[min,max]间随机数
     *
     * @param min
     * @param max
     * @return
     */
    private int getRangedRandom(int min, int max) {
        if (min == max) {
            return min;
        }
        min = min - 1;
        max = max - min;
        return getRandomOneToN(max) + min;
    }

    // 返回[1-n]间的一个随机整数
    private int getRandomOneToN(int n) {
        double tmp = Math.ceil(Math.random() * n);
        return (int) tmp;
    }
}

package game.core.drop;

import java.util.ArrayList;
import java.util.List;


/**
 * dropListData 实体类
 * create by GenerateJavaBeanUtil
 */
public class DropListData {


    /**
     * 掉落ID
     * create by GenerateJavaBeanUtil
     */
    private int dropListid = 0;


    /**
     * 随机次数
     * create by GenerateJavaBeanUtil
     */
    private int ranCount = 0;


    /**
     * 随机类型
     * create by GenerateJavaBeanUtil
     */
    private int randomType = 0;


    /**
     * 是否可重复
     * create by GenerateJavaBeanUtil
     */
    private int isRepeat = 0;


    /**
     * 内容数组
     * create by GenerateJavaBeanUtil
     */
    private List<Integer> itemID = new ArrayList<Integer>();


    /**
     * 内容类型
     * create by GenerateJavaBeanUtil
     */
    private List<Integer> dropType = new ArrayList<Integer>();


    /**
     * 随机权重数组
     * create by GenerateJavaBeanUtil
     */
    private List<Integer> dropRate = new ArrayList<Integer>();


    /**
     * 最小数量
     * create by GenerateJavaBeanUtil
     */
    private List<Integer> itemNumberMin = new ArrayList<Integer>();


    /**
     * 最大数量
     * create by GenerateJavaBeanUtil
     */
    private List<Integer> itemNumberMax = new ArrayList<Integer>();

    public void setDropListid(int dropListid) {
        this.dropListid = dropListid;
    }

    public int getDropListid() {
        return dropListid;
    }

    public void setRanCount(int ranCount) {
        this.ranCount = ranCount;
    }

    public int getRanCount() {
        return ranCount;
    }

    public void setRandomType(int randomType) {
        this.randomType = randomType;
    }

    public int getRandomType() {
        return randomType;
    }

    public void setIsRepeat(int isRepeat) {
        this.isRepeat = isRepeat;
    }

    public int getIsRepeat() {
        return isRepeat;
    }

    public void setItemID(List<Integer> itemID) {
        this.itemID = itemID;
    }

    public List<Integer> getItemID() {
        return itemID;
    }

    public void setDropType(List<Integer> dropType) {
        this.dropType = dropType;
    }

    public List<Integer> getDropType() {
        return dropType;
    }

    public void setDropRate(List<Integer> dropRate) {
        this.dropRate = dropRate;
    }

    public List<Integer> getDropRate() {
        return dropRate;
    }

    public void setItemNumberMin(List<Integer> itemNumberMin) {
        this.itemNumberMin = itemNumberMin;
    }

    public List<Integer> getItemNumberMin() {
        return itemNumberMin;
    }

    public void setItemNumberMax(List<Integer> itemNumberMax) {
        this.itemNumberMax = itemNumberMax;
    }

    public List<Integer> getItemNumberMax() {
        return itemNumberMax;
    }

}


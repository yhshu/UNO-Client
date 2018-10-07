package GUI.GameWindow;

import javax.swing.*;
import java.awt.*;

/**
 * 右侧信息面板
 */
public class InfoPanel extends JPanel {
    private String message;
    private String error;
    private int remainCards; // 剩余卡牌数
    private int panelCenter; // 面板中心
    private int[] playedCards; // 各玩家已打出牌数

    // setter

    public void setMessage(String message) {
        this.message = message;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setStatistic(int remainCards) {
        // todo playedCards
        this.remainCards = remainCards;
    }

    /**
     * 构造方法
     */
    public InfoPanel() {
        setPreferredSize(new Dimension(275, 200)); // 设定尺寸
        setOpaque(false);
    }

    /**
     * 绘制组件
     *
     * @param graphics 图形
     */
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        panelCenter = getWidth() / 2;

        paintMessage(graphics);
        paintError(graphics);
        paintStatistic(graphics);
    }

    /**
     * 绘制消息
     *
     * @param graphics 图形
     */
    private void paintMessage(Graphics graphics) {
        Font messageFont = new Font("Calibri", Font.BOLD, 25);
        FontMetrics fontMetrics = this.getFontMetrics(messageFont);
        int xPos = panelCenter - fontMetrics.stringWidth(message) / 2;

        graphics.setFont(messageFont);
        graphics.setColor(new Color(228, 108, 10));
        graphics.drawString(message, xPos, 75);
    }

    /**
     * 绘制错误
     *
     * @param graphics 图形
     */
    private void paintError(Graphics graphics) {
        if (error.isEmpty()) return;
        Font errorFont = new Font("Calibri", Font.PLAIN, 25);
        FontMetrics fontMetrics = this.getFontMetrics(errorFont);
        int xPos = panelCenter - fontMetrics.stringWidth(error) / 2;

        graphics.setFont(errorFont);
        graphics.setColor(Color.red);
        graphics.drawString(error, xPos, 35);

        error = "";
    }

    /**
     * 绘制统计信息
     *
     * @param graphics 图形
     */
    private void paintStatistic(Graphics graphics) {
        Font statisticFont = new Font("Calibri", Font.BOLD, 25);
        FontMetrics fontMetrics = this.getFontMetrics(statisticFont);
        graphics.setColor(new Color(127, 127, 127));

        String text = "已打出";
        int xPos = panelCenter - fontMetrics.stringWidth(text) / 2;

        graphics.setFont(statisticFont);
        graphics.drawString(text, xPos, 120);

        text = "剩余卡牌: " + remainCards;
        xPos = panelCenter - fontMetrics.stringWidth(text) / 2;
        graphics.drawString(text, xPos, 180);

        statisticFont = new Font("Calibri", Font.PLAIN, 20);
        graphics.setFont(statisticFont);
        fontMetrics = this.getFontMetrics(statisticFont);
        // 已打出数量
        // todo  text = "";
        xPos = panelCenter - fontMetrics.stringWidth(text) / 2;
    }
}
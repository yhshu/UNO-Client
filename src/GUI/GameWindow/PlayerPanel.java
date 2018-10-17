package GUI.GameWindow;

import Model.Player;
import Model.UNOCard;
import Util.OnlineUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerPanel extends JPanel {
    private Player player;

    private Box layout;
    private JLayeredPane cardHolder;
    private Box controlBox; // 控制区
    private JLabel nameLabel; // 用户名标签
    private ControlButtonHandler controlButtonHandler;

    // 控制区
    private JButton draw; // 抽牌
    private JButton sayUNO; // 说 UNO

    /* 构造方法 */

    public PlayerPanel(Player player) {
        // 组件初始化
        layout = Box.createHorizontalBox();
        cardHolder = new JLayeredPane();
        cardHolder.setPreferredSize(new Dimension(600, 175));

        // 设置变量
        this.player = player;
        setCards();
        setControlPanel();
        layout.add(cardHolder);
        layout.add(Box.createHorizontalStrut(40));
        layout.add(controlBox);
        add(layout);

        // 注册监听器
        if (isThisClient()) {
            controlButtonHandler = new ControlButtonHandler();
            draw.addActionListener(controlButtonHandler);
            sayUNO.addActionListener(controlButtonHandler);
        }
    }


    /* 成员方法 */

    /**
     * 设置卡牌区
     */
    public void setCards() {
        cardHolder.removeAll();

        // 原点在中心
        Point origin = getPoint(cardHolder.getWidth(), player.getMyCards().size());
        int offset = calculateOffset(cardHolder.getWidth(), player.getMyCards().size());

        int i = 0;
        for (UNOCard unoCard : player.getMyCards()) {
            CardPanel cardPanel;
            if (isThisClient()) {
                // 本客户的牌，另加一个 MouseListener，用于打出该牌
                cardPanel = new CardFrontPanel(unoCard);
                cardPanel.addMouseListener(new PlayerCardMouseAdapter());
            } else {
                // 不是本客户的牌，对该客户暂不可见
                cardPanel = new CardBackPanel(unoCard);
            }
            (cardPanel).setBounds(origin.x, origin.y, cardPanel.CARDSIZE.width, cardPanel.CARDSIZE.height);
            cardHolder.add(cardPanel, i++);
            cardHolder.moveToFront(cardPanel);
            origin.x += offset;
        }
        repaint();
    }

    /**
     * 设置控制区
     */
    public void setControlPanel() {
        if (isThisClient()) {
            draw = new JButton("抽牌");
            sayUNO = new JButton("说 UNO");

            // 设置组件属性
            draw.setBackground(new Color(79, 129, 189));
            draw.setFont(new Font("Arial", Font.BOLD, 20));
            draw.setFocusable(false);

            sayUNO.setBackground(new Color(149, 55, 53));
            sayUNO.setFont(new Font("Arial", Font.BOLD, 20));
            sayUNO.setFocusable(false);
        }

        nameLabel = new JLabel(player.getUsername());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));

        controlBox = Box.createVerticalBox();
        controlBox.add(nameLabel);

        if (isThisClient()) {
            // 添加组件到控制区
            controlBox.add(draw);
            controlBox.add(Box.createVerticalStrut(15));
            controlBox.add(sayUNO);
        }
    }

    /**
     * 计算偏移
     *
     * @param width     卡牌区宽度
     * @param cardTotal 卡牌数量
     * @return 偏移量
     */
    private int calculateOffset(int width, int cardTotal) {
        int offset = 71;
        if (cardTotal <= 8) {
            return offset;
        } else {
            double off = (width - 100) / (cardTotal - 1);
            return (int) off;
        }
    }

    /**
     * 获得卡牌区的原点
     *
     * @param width     卡牌区宽度
     * @param cardTotal 卡牌数量
     * @return 卡牌区原点
     */
    private Point getPoint(int width, int cardTotal) {
        Point point = new Point(0, 20);
        if (cardTotal >= 8) {
            return point;
        } else {
            point.x = (width - calculateOffset(width, cardTotal) * cardTotal) / 2;
            return point;
        }
    }

    /* 子类 */

    /**
     * 控制按钮监听器
     */
    class ControlButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (player.isMyTurn()) {
                if (e.getSource().equals(draw)) {
                    player.drawCard();
                } else if (e.getSource().equals(sayUNO))
                    player.sayUNO();
            }
        }
    }

    private boolean isThisClient() {
        return player.getUsername().equals(OnlineUtil.getUsername());
    }
}

package GUI.GameWindow;

import Model.GameTable;
import Model.Player;
import Util.OnlineUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class GamePanel extends JPanel {
    private PlayerPanel playerPanel1; // 对方
    private PlayerPanel playerPanel2; // 本客户
    private TablePanel tablePanel; // 牌桌

    public GamePanel(GameTable gameTable) {
        int remainCardNum = gameTable.getRemainCardNum();
        List<Player> playerList = gameTable.getPlayers();
        CardFrontPanel firstCardPanel = new CardFrontPanel(gameTable.getTopCard());

        setPreferredSize(new Dimension(960, 720));
        setBackground(new Color(30, 36, 40));
        setLayout(new BorderLayout());

        for (Player player : playerList) {
            if (player.getUsername().equals(OnlineUtil.getUsername())) { // 该 player 对象对应本用户
                playerPanel2 = new PlayerPanel(player);
            } else {
                playerPanel1 = new PlayerPanel(player);
            }
        }
        tablePanel = new TablePanel(remainCardNum, firstCardPanel);
        playerPanel1.setOpaque(false);
        playerPanel2.setOpaque(false);

        add(playerPanel1, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(playerPanel2, BorderLayout.SOUTH);
    }

    /* getter & setter */

    public PlayerPanel getPlayerPanel1() {
        return playerPanel1;
    }

    public void setPlayerPanel1(PlayerPanel playerPanel1) {
        this.playerPanel1 = playerPanel1;
    }

    public PlayerPanel getPlayerPanel2() {
        return playerPanel2;
    }

    public void setPlayerPanel2(PlayerPanel playerPanel2) {
        this.playerPanel2 = playerPanel2;
    }

    public TablePanel getTablePanel() {
        return tablePanel;
    }

    public void setTablePanel(TablePanel tablePanel) {
        this.tablePanel = tablePanel;
    }

    public void refreshPanel(GameTable gameTable) {
        for (Player player : gameTable.getPlayers()) {
            // 设置卡牌
            if (OnlineUtil.isThisClient(player)) {
                playerPanel2.setCards(player);
            } else {
                playerPanel1.setCards(player);
            }
            // 设置轮次
            if (player.isMyTurn()) {
                if (OnlineUtil.isThisClient(player))
                    this.tablePanel.getInfoPanel().setMessageOnPanel("轮到您");
                else this.tablePanel.getInfoPanel().setMessageOnPanel("轮到 " + player.getUsername());
            }
            // 设置错误信息
            this.tablePanel.getInfoPanel().setErrorOnPanel("");
        }
        // 设置牌桌背景色和最近一张打出的牌
        tablePanel.setPlayedCard(new CardFrontPanel(gameTable.getTopCard()));
        tablePanel.getTable().setBackground(gameTable.getTableBackgroundColor());

        // 业务无关
        tablePanel.revalidate();
        this.revalidate();
    }
}

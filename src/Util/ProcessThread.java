package Util;

import GUI.GameWindow.CardFrontPanel;
import GUI.GameWindow.GameFrame;
import GUI.HallFrame;
import Model.GameTable;
import Model.Player;
import Model.UNOCard;
import Service.GameService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProcessThread extends Thread {
    private static Gson gson = new Gson();
    private static GameService gameService = new GameService();
    private static Type UNOCardType = new TypeToken<UNOCard>() {
    }.getType();
    private static Type playerType = new TypeToken<Player>() {
    }.getType();

    /**
     * 处理消息队列的线程主循环
     */
    public void run() {
        OnlineUtil.readyToProcess = true;
        gson = new Gson();
        System.out.println("[" + TimeUtil.getTimeInMillis() + "] ProcessThread has started");
        //noinspection InfiniteLoopStatement
        while (true) {
            String msg = OnlineUtil.getMessageList().poll(); // 阻塞队列
            if (msg != null) {
                processMsg(msg);
            }
        }
    }

    /**
     * 处理消息
     *
     * @param msg 消息
     */
    private static void processMsg(String msg) {
        if (msg.startsWith("uno01 login")) { // 登录反馈
            login(msg);
        } else if (msg.startsWith("uno02 hall")) { // 服务器单播的大厅消息
            setGameTablesData(msg);
        } else if (msg.startsWith("uno02 enterroom")) { // 进入房间反馈
            enterRoom(msg);
        } else if (msg.startsWith("uno02 roomstatus")) { // 服务器广播的房间状态，如果进入房间失败不会广播
            setRoomStatus(msg);
        } else if (msg.startsWith("uno02 gamestart")) { // 游戏初始化
            gameStartResponse(msg);
        } else if (msg.startsWith("uno02 drawcard")) { // 抽牌操作
            drawCardResponse(msg);
        } else if (msg.startsWith("uno02 turn")) { // 游戏轮次
            nextTurnResponse(msg);
        } else if (msg.startsWith("uno02 remaincard")) {
            remainCardResponse(msg);
        } else if (msg.startsWith("uno02 playcard")) {
            playCardResponse(msg);
        }
    }

    private static void gameStartResponse(String msg) {
        msg = msg.substring(0, msg.length() - 2); // 去除字符串末尾 \r\n
        String[] msgSplit = msg.split(" ");
        int remainCardNum = Integer.parseInt(msgSplit[2]);

        // 将字符串组装为参数
        UNOCard firstCard = gson.fromJson(msgSplit[3], UNOCardType);
        List<Player> playerList = new CopyOnWriteArrayList<>();
        for (int i = 4; i < msgSplit.length; i++) {
            Player player = gson.fromJson(msgSplit[i], playerType);
            playerList.add(player);
        }
        gameService.gameStart(remainCardNum, firstCard, playerList);
    }

    /**
     * 登录反馈
     *
     * @param msg 消息
     */
    private static void login(String msg) {
        synchronized (OnlineUtil.messageLock) {
            msg = msg.substring(0, msg.length() - 2); // 去除字符串末尾 \r\n
            String[] msgSplit = msg.split(" ");
            if (msgSplit[1].equals("login")) {
                if (msgSplit[3].equals("1")) { // 登录成功
                    OnlineUtil.username = msgSplit[2];
                }
            }
            OnlineUtil.messageLock.notify();
        }
    }

    /**
     * 如果未收到大厅数据，不置 data
     *
     * @param msg 大厅数据
     */
    private static void setGameTablesData(String msg) {
        synchronized (OnlineUtil.messageLock) {
            String[][] data = new String[GameConstants.roomNum][3];
            String[] msgSplit = msg.split("\r\n\r\n");
            int i = 0;

            if (msgSplit.length == 1) {
                // 大厅数据为空
                OnlineUtil.messageLock.notify();
                return;
            }

            // 大厅数据非空
            String[] content = msgSplit[1].split("\r\n");
            for (String line : content) {
                String[] line_split = line.split(",");
                line_split[2] = decodeRoomStatus(line_split[2]);
                System.arraycopy(line_split, 0, data[i], 0, 3);
                i++;
            }
            HallFrame.setData(data);
            OnlineUtil.messageLock.notify();
        }

    }

    /**
     * 解析房间状态
     *
     * @param status 截取的消息
     * @return 房间状态
     */
    private static String decodeRoomStatus(String status) {
        switch (status) {
            case "0":
                return "空闲";
            case "1":
                return "等待";
            case "2":
                return "游戏中";
            default:
                break;
        }
        return "未知";
    }

    /**
     * 处理对进入房间请求的反馈
     *
     * @param msg 进入房间反馈
     */
    private static void enterRoom(String msg) {
        msg = msg.substring(0, msg.length() - 2); // 去除字符串末尾 \r\n
        String[] msgSplit = msg.split(" ");

        if (msgSplit[3].equals("1")) { // 服务器：进入房间成功
            OnlineUtil.setRoomNum(msgSplit[2]); // 设置客户端房间号
            // 进一步处理在 setRoomStatus 方法中
        } else if (msgSplit[3].equals("0")) { // 服务器：进入房间失败
            OnlineUtil.setRoomNum(null);
            JOptionPane.showMessageDialog(null, "请稍后重试...", "进入房间", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 修改游戏房间的用户名
     *
     * @param msg 服务器广播的房间状态
     */
    private static void setRoomStatus(String msg) {
        msg = msg.substring(0, msg.length() - 2); // 去除字符串末尾 \r\n
        String[] msgSplit = msg.split(" ");

        String roomStatus = msgSplit[3];
        int roomNum = Integer.parseInt(msgSplit[2]);
        String[] roomStatusSplit = roomStatus.split(",");
        // 修改 JTable 中的房间信息，包括用户名和房间状态
        roomStatusSplit[2] = decodeRoomStatus(roomStatusSplit[2]);
        for (int i = 0; i < roomStatusSplit.length; i++) {
            HallFrame.getGameTableModel().setValueAt(roomStatusSplit[i], roomNum, i);
        }
    }

    private static void drawCardResponse(String msg) {
        msg = msg.substring(0, msg.length() - 2); // 去除字符串末尾 \r\n
        String[] msgSplit = msg.split(" ");
        try {
            String username = msgSplit[2];
            UNOCard newCard = gson.fromJson(msgSplit[3], UNOCardType);

            // 模型层
            GameTable gameTable = GameService.getGameTable();
            Player player = gameTable.getPlayerByUsername(username);
            player.getMyCards().add(newCard);
            // 视图层
            GameFrame.getGamePanel().refreshPanel(GameService.getGameTable());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ProcessThread: draw card response exception");
        }
    }

    private static void nextTurnResponse(String msg) {
        msg = msg.substring(0, msg.length() - 2); // 去除字符串末尾 \r\n
        String[] msgSplit = msg.split(" ");

        String username = msgSplit[2];
        // 模型层
        try {
            GameService.getGameTable().setTurn(username);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ProcessThread: nextTurnResponse exception");
        }
        // 视图层
        Player player = GameService.getGameTable().getPlayerByUsername(username);
        if (OnlineUtil.isThisClient(player))
            GameFrame.getGamePanel().getTablePanel().getInfoPanel().setMessage("轮到您");
        else
            GameFrame.getGamePanel().getTablePanel().getInfoPanel().setMessage("轮到 " + username);
    }

    private static void remainCardResponse(String msg) {
        msg = msg.substring(0, msg.length() - 2);
        String[] msgSplit = msg.split(" ");

        int remainCardNum = Integer.parseInt(msgSplit[2]);
        // 模型层
        try {
            GameService.getGameTable().setRemainCardNum(remainCardNum);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ProcessThread: remainCardResponse exception");
        }
        // 视图层
        GameFrame.getGamePanel().getTablePanel().getInfoPanel().setRemainCardNum(remainCardNum);
    }

    private static void playCardResponse(String msg) {// uno02 playcard username topCardJson playerJson
        msg = msg.substring(0, msg.length() - 2);
        String[] msgSplit = msg.split(" ");

        // 将字符串组装为参数
        String username = msgSplit[2];
        String topCardJson = msgSplit[3];
        UNOCard topCard = gson.fromJson(topCardJson, UNOCardType);
        List<Player> playerList = new CopyOnWriteArrayList<>();
        for (int i = 4; i < msgSplit.length; i++) {
            Player player = gson.fromJson(msgSplit[i], playerType);
            playerList.add(player);
        }

        // 模型层
        GameService.getGameTable().setPlayers(playerList);
        // 视图层
        GameFrame.getGamePanel().refreshPanel(GameService.getGameTable()); // 修改玩家手中的牌
        GameFrame.getGamePanel().getTablePanel().setPlayedCard(new CardFrontPanel(topCard)); // 修改牌桌上的牌

//        Player player = GameService.getGameTable().getPlayerByUsername(username);
//        if (player.getMyCards().size() == 1 && !player.isSaidUNO()) {
//            GameFrame.getGamePanel().getTablePanel().getInfoPanel().setError(username +" 忘记说 UNO 啦");
//        }

//        if (p.getTotalCards() == 1 && !p.getSaidUNO()) {
//            infoPanel.setError(p.getName() + " Forgot to say UNO");
//            p.obtainCard(getCard());
//            p.obtainCard(getCard());
//        }else if(p.getTotalCards()>2){
//            p.setSaidUNOFalse();
//        }
    }

}

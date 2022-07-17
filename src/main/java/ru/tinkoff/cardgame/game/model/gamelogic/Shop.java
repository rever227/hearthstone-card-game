package ru.tinkoff.cardgame.game.model.gamelogic;

import lombok.Data;
import ru.tinkoff.cardgame.game.model.card.Card;
import ru.tinkoff.cardgame.game.model.card.CardProvider;

import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class Shop {

    private static final int SHOP_CARD_NUMBER = 3;
    private static final int MAX_SHOP_LEVEL = 5;
    private static final int BASE_VALUE_UPGRADE_PRICE = 5;

    private final CopyOnWriteArrayList<Card> cardList;
    private boolean freezeStatus = false;
    private int level = 1;
    private int upgradePrice = BASE_VALUE_UPGRADE_PRICE;

    public Shop() {
        this.cardList = new CopyOnWriteArrayList<>();
        this.freezeStatus = false;
    }

    public void upgradeLevel() {
        if (this.level < MAX_SHOP_LEVEL) {
            this.level++;
            this.upgradePrice = BASE_VALUE_UPGRADE_PRICE + this.level - 1;
        }
    }

    public void updateShop() {
        if (!isFreezeStatus()) {
            this.cardList.clear();
            for (int i = 0; i < SHOP_CARD_NUMBER; i++) {
                this.cardList.add(CardProvider.INSTANCE.getRandomLvlCard(this.level));
            }
        }
    }

    public Card buyCard(int index) {
        return this.cardList.remove(index);
    }

    public void decreaseUpgradePrice() {
        if (upgradePrice > 1) {
            upgradePrice--;
        }
    }
}
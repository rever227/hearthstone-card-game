package ru.tinkoff.cardgame.game.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.cardgame.game.model.card.Card;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Round {
    private final Player firstPlayer;
    private final Player secondPlayer;

    public Round(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }


    //
    //  11.07
    //
    public List<Player> startRound(){
        List<Player> players = new ArrayList<>();
        players.add(firstPlayer);
        players.add(secondPlayer);
        Collections.shuffle(players);
        players = Attack(players);
        return players;
    }



    private int getRandom(int max){
        int min = 0;
        return (int)((Math.random() * ((max - min) + 1)) + min);
    }

    private int getCountMoves(List<Card> c1, List<Card> c2 ){
        return (c1.size()+c2.size());
    }




    private List<Player> Attack(List<Player> players) {

        List<Card> cardsOfAttack = players.get(0).getActiveCards();
        List<Card> cardsOfDefence = players.get(1).getActiveCards();


        if (cardsOfAttack.size() == 0) {

            //Карты только у защиты(Атака защиты(1) по герою Атаки(0))

            int damageForHero = 0;
            for (Card card : cardsOfDefence) {
                damageForHero += card.getLvl();
            }
            damageForHero += players.get(1).getShop().getLvl();// добавляем уровень магазина игрока Защиты к урону
            if (players.get(0).getHp() <= damageForHero) { //проверяем хватает ли урона на убийство
                //TODO: send to front death\lose
                players.get(0).setHp(0); // устанавливаем хп в 0
            } else {
                //TODO: send to front get damage
                players.get(0).setHp(players.get(0).getHp() - damageForHero); //наносим урон
            }

        } else if (cardsOfDefence.size() == 0) {

            //Карты только у атаки (Герой атаки(0) атакует героя защиты(1))

            int damageForHero = 0;
            for (Card card : cardsOfAttack) {
                damageForHero += card.getLvl();
            }
            damageForHero += players.get(0).getShop().getLvl();// добавляем уровень магазина игрока Защиты к урону
            if (players.get(1).getHp() <= damageForHero) { //проверяем хватает ли урона на убийство
                //TODO: send to front death\lose
                players.get(1).setHp(0); // устанавливаем хп в 0
            } else {
                //TODO: send to front get damage
                players.get(1).setHp(players.get(1).getHp() - damageForHero); //наносим урон
            }


        } else {
            //TODO: У двоих есть карты
            //TODO: Удаление карты при атаке(хп атакующей меньше атакуемой)
            int attackIndex = 0;
            int defenceIndex = 0;
            Map<Card, Boolean> map = new HashMap<>();
            for (Card card : cardsOfAttack) {
                map.put(card, false);
            }
            int countOfAllCards = getCountMoves(cardsOfAttack, cardsOfDefence);
            while (cardsOfAttack.size() == 1 && cardsOfDefence.size() == 1) {
                Game.logger.info("While");
                Game.logger.info("A|Count of cards = " + cardsOfAttack.size());

                Game.logger.info("D|Count of cards = " + cardsOfDefence.size());
                //Атака атаки

                int index = getRandom(cardsOfDefence.size() - 1); //индекс атакуемой карты
                Game.logger.info("index = " + index);
                Game.logger.info("AttackIndex = " + attackIndex);
                Card attackCard = cardsOfAttack.get(attackIndex);  //карта наносящая урон
                Card attackedCard = cardsOfDefence.get(index); // карта получающая урон

                int hp = attackedCard.getHp();
                int damage = attackCard.getDamage();
                int hpA = attackCard.getHp();
                int damageD = attackedCard.getDamage();

                if (hp <= damage) { //если хп карты(например 1) <= чем урон ( например 2), то удаляем карту
                    //TODO: Отправка на фронт события "уничтожение карты"
                    if (hpA <= damageD) {
                        cardsOfAttack.remove(attackIndex);
                    } else {
                        attackCard.setHp(hpA - damageD);
                        cardsOfAttack.set(attackIndex, attackCard);
                    }
                    cardsOfDefence.remove(index);
                    if (index < defenceIndex) {
                        defenceIndex--;
                    }


                } else {
                    //TODO: Отправка на фронт события "получение урона"
                    if (hpA <= damageD) {
                        cardsOfAttack.remove(attackIndex);
                    } else {
                        attackCard.setHp(hpA - damageD);
                        cardsOfAttack.set(attackIndex, attackCard);
                    }
                    attackedCard.setHp(hp - damage); //иначе наносим урон
                    cardsOfDefence.set(index, attackedCard); // и заменяем старую карту, на новую с измененными характеристиками

                    Game.logger.info("Урон нанесен картой - " + attackCard.toString() + " по карте - " + attackedCard.toString());
                }

                attackIndex++;

                //Атака защиты

                index = getRandom(cardsOfAttack.size() - 1); //индекс атакуемой карты
                Game.logger.info("index = " + index);
                attackCard = cardsOfDefence.get(defenceIndex);  //карта наносящая урон
                attackedCard = cardsOfAttack.get(index); // карта получающая урон

                hp = attackedCard.getHp();
                damage = attackCard.getDamage();
                hpA = attackCard.getHp();
                damageD = attackedCard.getDamage();


                if (hp <= damage) { //если хп карты(например 1) <= чем урон ( например 2), то удаляем карту
                    //TODO: Отправка на фронт события "уничтожение карты"
                    if (hpA <= damageD) {
                        cardsOfDefence.remove(defenceIndex);
                    } else {
                        attackCard.setHp(hpA - damageD);
                        cardsOfDefence.set(attackIndex, attackCard);
                    }
                    cardsOfAttack.remove(index);
                    if (index < attackIndex) {
                        attackIndex--;


                    } else {
                        //TODO: Отправка на фронт события "получение урона"
                        if (hpA <= damageD) {
                            cardsOfDefence.remove(defenceIndex);
                        } else {
                            attackCard.setHp(hpA - damageD);
                            cardsOfDefence.set(attackIndex, attackCard);
                        }
                        attackedCard.setHp(hp - damage); //иначе наносим урон
                        cardsOfAttack.set(index, attackedCard); // и заменяем старую карту, на новую с измененными характеристиками

                    }
                    Game.logger.info("DefenceIndex = " + defenceIndex);
                    defenceIndex++;


                }

                if (cardsOfDefence.size() == 0 && cardsOfAttack.size()!=0) {
                    int damageForHero = 0;
                    for (Card card : cardsOfAttack) {
                        damageForHero += card.getLvl();
                    }
                    damageForHero += players.get(0).getShop().getLvl();
                    players.get(1).setHp(players.get(1).getHp() - damageForHero);
                    //TODO: End round
                    break;

                }else if(cardsOfAttack.size()==0 && cardsOfDefence.size()!=0){
                    int damageForHero = 0;
                    for (Card card : cardsOfDefence) {
                        damageForHero += card.getLvl();
                    }
                    damageForHero += players.get(1).getShop().getLvl();
                    players.get(0).setHp(players.get(0).getHp() - damageForHero);
                    //TODO: End round
                    break;
                }

            if(cardsOfAttack.size()==0 && cardsOfDefence.size()==0){
                //TODO: Ничья
                break;
            }
            if(attackIndex == cardsOfAttack.size()){
                attackIndex = 0;
            }
            if(defenceIndex == cardsOfDefence.size()){
                    defenceIndex = 0;
                }
                //последняя карта(сброс цикла)

            //TODO: вернуть на фронт конец раунда

        }}
        return players;
    }





    //
    //  11.07
    //


    @Override
    public String toString() {
        return "\nRound{" +
                "\n\tfirstPlayer = " + firstPlayer +
                ", \n\tsecondPlayer = " + secondPlayer +
                '}';
    }
}

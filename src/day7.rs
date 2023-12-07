use std::cmp::Ordering;
use std::fmt::Debug;
use std::fs;

use itertools::Itertools;
use multiset::HashMultiSet;

use crate::day7::HandType::{FiveOfAKind, FourOfAKind, FullHouse, OnePair, ThreeOfAKind, TwoPair};

pub fn run() {
    let raw_input: String =
        fs::read_to_string("input/day7.txt").expect("Unable to read input file");
    let input: Vec<&str> = raw_input.lines().collect();

    let hands: Vec<Hand> = input
        .iter()
        .map(|line| {
            let split = line.split_once(' ').unwrap();
            return Hand {
                hand: split
                    .0
                    .chars()
                    .map(|x| rank_to_card(x))
                    .collect::<Vec<Card>>()
                    .try_into()
                    .unwrap(),
                bid: split.1.parse().unwrap(),
            };
        })
        .sorted_by(|left, right| compare_hand(left, right, false))
        .rev()
        .collect();

    let total_winnings: u64 = hands
        .iter()
        .enumerate()
        .map(|(rank, hand)| (rank as u64 + 1) * hand.bid)
        .sum();
    println!("Total winnings: {}", total_winnings);

    let hands: Vec<Hand> = hands
        .iter()
        .cloned()
        .sorted_by(|left, right| compare_hand(left, right, true))
        .rev()
        .collect();

    let total_winnings: u64 = hands
        .iter()
        .enumerate()
        .map(|(rank, hand)| (rank as u64 + 1) * hand.bid)
        .sum();
    println!("Total winnings: {}", total_winnings)
}

#[derive(Debug, Eq, PartialEq, Clone)]
struct Hand {
    hand: [Card; 5],
    bid: u64,
}

fn compare_hand(first: &Hand, second: &Hand, jokers: bool) -> Ordering {
    let first_type = parse_hand_type(&first.hand, jokers);
    let second_type = parse_hand_type(&second.hand, jokers);
    if first_type.eq(&second_type) {
        return compare_card(&first.hand[0], &second.hand[0], jokers)
            .then(compare_card(&first.hand[1], &second.hand[1], jokers))
            .then(compare_card(&first.hand[2], &second.hand[2], jokers))
            .then(compare_card(&first.hand[3], &second.hand[3], jokers))
            .then(compare_card(&first.hand[4], &second.hand[4], jokers))
            .reverse();
    }
    return first_type.cmp(&second_type);
}

fn compare_card(first: &Card, second: &Card, jokers: bool) -> Ordering {
    if jokers {
        return match (first, second) {
            (Card::JACK, Card::JACK) => Ordering::Equal,
            (Card::JACK, _) => Ordering::Less,
            (_, Card::JACK) => Ordering::Greater,
            _ => first.cmp(second),
        };
    }
    first.cmp(second)
}

fn parse_hand_type(hand: &[Card; 5], jokers: bool) -> HandType {
    let mut hand = hand.clone();
    hand.sort();
    let hand = hand;
    let mut multiset: HashMultiSet<Card> = hand.iter().cloned().collect();

    if jokers {
        // Remove the jokers first
        let jokers = multiset.count_of(&Card::JACK) as u64;
        multiset.remove_all(&Card::JACK);
        if let Some(largest_amount_of_cards) = multiset
            .iter()
            .max_by(|a, b| multiset.count_of(a).cmp(&multiset.count_of(b)))
        {
            multiset.insert_times(*largest_amount_of_cards, jokers as usize);
        } else {
            multiset.insert_times(Card::ACE, jokers as usize);
        }
    }

    let singles = multiset
        .iter()
        .filter(|x| multiset.count_of(x) == 1)
        .count();

    if multiset.iter().any(|x| multiset.count_of(x) == 5) {
        return FiveOfAKind;
    }
    if multiset.iter().any(|x| multiset.count_of(x) == 4) {
        return FourOfAKind;
    }
    let has_3 = multiset.iter().any(|x| multiset.count_of(x) == 3);
    let has_2 = multiset.iter().any(|x| multiset.count_of(x) == 2);
    if has_3 {
        return if has_2 { FullHouse } else { ThreeOfAKind };
    }
    if has_2 {
        return if singles == 1 { TwoPair } else { OnePair };
    }
    return HandType::HighCard;
}

#[derive(Eq, PartialEq, Ord, PartialOrd, Clone, Copy, Debug)]
#[allow(dead_code)]
enum HandType {
    FiveOfAKind,
    FourOfAKind,
    FullHouse,
    ThreeOfAKind,
    TwoPair,
    OnePair,
    HighCard,
}

#[derive(Debug, Eq, PartialEq, PartialOrd, Ord, Copy, Clone, Hash)]
enum Card {
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING,
    ACE,
}

fn rank_to_card(input: char) -> Card {
    match input {
        'A' => Card::ACE,
        'K' => Card::KING,
        'Q' => Card::QUEEN,
        'J' => Card::JACK,
        'T' => Card::TEN,
        '9' => Card::NINE,
        '8' => Card::EIGHT,
        '7' => Card::SEVEN,
        '6' => Card::SIX,
        '5' => Card::FIVE,
        '4' => Card::FOUR,
        '3' => Card::THREE,
        '2' => Card::TWO,
        _ => panic!("What?"),
    }
}

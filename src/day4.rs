use std::collections::HashMap;
use std::fs;

pub fn run() {
    let raw_input: String =
        fs::read_to_string("input/day4.txt").expect("Unable to read input file");
    let input: Vec<&str> = raw_input.lines().collect();

    #[derive(Clone)]
    struct Card {
        winning_numbers: Vec<u32>,
        play_numbers: Vec<u32>,
    }

    let mut cards: Vec<Card> = vec![];

    for line in input.iter() {
        // Remove card number (we infer it from the index)
        let colon = line.find(':').unwrap();
        let line = &line[colon + 1..];

        let split = line.split_once(" | ").unwrap();
        let winning_numbers = parse_numbers(split.0);
        let play_numbers = parse_numbers(split.1);

        cards.push(Card {
            winning_numbers,
            play_numbers,
        });
    }

    // PART 1

    let mut cards_to_count: HashMap<u32, u32> = HashMap::new();
    let mut total_points = 0;

    for (id, card) in cards.iter().enumerate() {
        // To simplify logic, we begin at one, and shift right for each winning number
        // Afterwards, we shift left once to remove the one
        let winning_count = card
            .play_numbers
            .iter()
            .filter(|x| card.winning_numbers.contains(x))
            .count();
        let card_points: u32 = (1 << winning_count) >> 1;
        total_points += card_points;

        cards_to_count.insert(id as u32, winning_count as u32);
    }

    println!("Total points: {total_points}");

    // PART 2

    let mut processed_cards_amount: u32 = 0;
    let mut cards_to_process: Vec<u32> = (0..cards.len()).map(|x| x as u32).collect();

    while !cards_to_process.is_empty() {
        let card_id: &u32 = &cards_to_process.pop().unwrap();
        let winning_amount: u32 = cards_to_count[card_id];
        for x in 0..winning_amount {
            let card_id = card_id + x + 1;
            cards_to_process.push(card_id);
        }
        processed_cards_amount += 1;
    }

    println!("Final count of scratchcards: {processed_cards_amount}")
}

fn parse_numbers(seq: &str) -> Vec<u32> {
    seq.split_whitespace().map(|x| x.parse().unwrap()).collect()
}

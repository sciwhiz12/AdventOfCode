use std::fs;

use itertools::Itertools;

pub fn run() {
    let raw_input: String =
        fs::read_to_string("input/day9.txt").expect("Unable to read input file");
    let input: Vec<&str> = raw_input.lines().collect();

    let mut predictions_sum = 0;
    let mut backwards_extrapolation_sum = 0;
    for line in input {
        let values: Vec<i128> = line
            .split_whitespace()
            .map(|x| x.parse().unwrap())
            .collect_vec();

        let mut sequences: Vec<Vec<i128>> = vec![values.clone()];
        let mut seq = values;
        while seq.iter().any(|x| *x != 0) {
            seq = calculate_sequence(&seq);
            sequences.push(seq.clone())
        }

        // sequences.iter().for_each(|x| println!("{:?}", x));

        // PART 1
        let mut prediction = 0;
        for seq in sequences.iter().rev().skip(1) {
            let last = seq.last().unwrap();
            prediction += last;
        }
        predictions_sum += prediction;

        // PART 2
        let mut prev = 0;
        for seq in sequences.iter().rev().skip(1) {
            let last = seq.first().unwrap();
            let prediction = last - prev;
            prev = prediction;
        }
        backwards_extrapolation_sum += prev
    }
    println!("Sum of predictions: {}", predictions_sum);
    println!(
        "Sum of backwards extrapolations: {}",
        backwards_extrapolation_sum
    );
}

fn calculate_sequence(input: &Vec<i128>) -> Vec<i128> {
    let mut out = vec![];
    for window in input.windows(2) {
        out.push(window[1] - window[0]);
    }
    out
}

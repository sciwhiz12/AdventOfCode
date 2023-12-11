use std::cmp::max;
use std::{cmp, fs};

use num::abs;

type Position = (usize, usize);

pub fn run() {
    let raw_input: String =
        fs::read_to_string("input/day11.txt").expect("Unable to read input file");
    let input: Vec<&str> = raw_input.lines().collect();

    let sum_part_1 = calculate(input.clone(), 2);
    println!(
        "Sum of shortest distances between all galaxy pairs (part 1): {}",
        sum_part_1
    );

    let sum_part_2 = calculate(input.clone(), 1_000_000);
    println!(
        "Sum of shortest distances between all galaxy pairs (part 2): {}",
        sum_part_2
    );

    // Print map
    // for (y, row) in map.iter().enumerate() {
    //     for (x, cell) in row.iter().enumerate() {
    //         print!("{cell}");
    //     }
    //     println!()
    // }
}

fn calculate(input: Vec<&str>, empty_expansion: u64) -> u64 {
    let galaxy_slot = '#';

    println!("    Building map");
    let mut column_tracker: Vec<u64> = vec![0; input[0].len()];
    let mut row_tracker: Vec<u64> = vec![];
    let mut map: Vec<Vec<char>> = vec![];
    for line in input.iter() {
        let mut row = vec![];
        let mut galaxies = 0;
        for (x, char) in line.chars().enumerate() {
            if char == galaxy_slot {
                column_tracker[x] += 1;
                galaxies += 1;
            }
            row.push(char);
        }
        map.push(row.clone());
        row_tracker.push(galaxies);
    }

    println!("    Finding galaxies");
    // Find galaxies
    let mut galaxies: Vec<Position> = vec![];
    for (y, row) in map.iter().enumerate() {
        for (x, cell) in row.iter().enumerate() {
            if *cell == galaxy_slot {
                galaxies.push((x, y));
            }
        }
    }

    println!("    Calculating distances");
    let mut to_process = galaxies.clone();
    let mut distance_sum = 0;
    while let Some(galaxy) = to_process.pop() {
        for other in &to_process {
            // Calculate intervening empty columns and rows
            let mut empty_columns = 0;
            for col in cmp::min(galaxy.0, other.0)..max(galaxy.0, other.0) {
                if column_tracker[col] == 0 {
                    empty_columns += 1;
                }
            }
            let mut empty_rows = 0;
            for row in cmp::min(galaxy.1, other.1)..max(galaxy.1, other.1) {
                if row_tracker[row] == 0 {
                    empty_rows += 1;
                }
            }

            let dx = abs(galaxy.0 as isize - other.0 as isize) as usize
                + (empty_columns * (empty_expansion - 1)) as usize;
            let dy = abs(galaxy.1 as isize - other.1 as isize) as usize
                + (empty_rows * (empty_expansion - 1)) as usize;
            let distance = dx + dy;
            distance_sum += distance;
        }
    }
    return distance_sum as u64;
}

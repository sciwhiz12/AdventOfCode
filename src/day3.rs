use std::collections::HashMap;
use std::fs;

type Position = (usize, usize);
type ID = usize;

pub fn run() {
    let raw_input: String =
        fs::read_to_string("input/day3.txt").expect("Unable to read input file");
    let input: Vec<&str> = raw_input.lines().collect();

    // We will iterate over the input twice
    // First: build a map of (line_num, char_index) positions to IDs, and a vector of numbers (IDs to number)
    let mut position_to_id: HashMap<Position, ID> = HashMap::new();
    let mut id_to_number: Vec<usize> = vec![];

    for (line_num, line) in input.iter().enumerate() {
        let tracked_positions: &mut Vec<Position> = &mut vec![];
        let mut number_builder: String = String::new();

        for (idx, ch) in line.char_indices() {
            if ch.is_numeric() {
                tracked_positions.push((line_num, idx));
                number_builder.push(ch);
            } else {
                try_assign_id(
                    &mut number_builder,
                    &mut position_to_id,
                    &mut id_to_number,
                    tracked_positions,
                );
            }
        }

        try_assign_id(
            &mut number_builder,
            &mut position_to_id,
            &mut id_to_number,
            tracked_positions,
        );
    }

    // Second: find symbols, check neighbors if they exist in the map, and add those which are in
    // the map to a list, to sum later
    let mut part_ids: Vec<ID> = vec![];
    let mut gear_ratio_sum = 0;

    let max: Position = (input.len() - 1, input.get(0).unwrap().len() - 1);
    for (line_num, line) in input.iter().enumerate() {
        for (char_pos, ch) in line.char_indices() {
            if ch == '.' || ch.is_numeric() {
                continue;
            }

            let origin: Position = (line_num, char_pos);
            // println!("Checking {origin:?} with symbol {ch}");
            let mut ids: Vec<ID> = vec![];
            // Check every neighboring position
            for y in [-1, 0, 1] {
                for x in [-1, 0, 1] {
                    if let Some(pos) = calculate_position(origin, max, y, x) {
                        // println!("\t{pos:?}");
                        if let Some(id) = position_to_id.get(&pos) {
                            ids.push(*id)
                        }
                    }
                }
            }

            ids.sort();
            ids.dedup();
            // println!("For {origin:?}, found IDs {ids:?}");

            // PART 2:
            if ch == '*' && ids.len() == 2 {
                let first = id_to_number.get(ids[0]).unwrap();
                let second = id_to_number.get(ids[1]).unwrap();
                let gear_ratio = first * second;
                // println!("Found gear at {origin:?} with ratio {gear_ratio} ({first} * {second})");
                gear_ratio_sum += gear_ratio;
            }

            // This moves the values over, so we have to do PART 2 before it
            part_ids.append(&mut ids);
        }
    }

    part_ids.sort();
    // println!("Found part IDs {part_ids:?}");
    let part_numbers_sum: usize = part_ids.iter().map(|x| id_to_number.get(*x).unwrap()).sum();
    println!("Sum of all part numbers: {part_numbers_sum}");
    println!("Sum of all gear ratios: {gear_ratio_sum}")

    // DEBUG
    // let tracked_positions: Vec<&Position> = position_to_id
    //     .iter()
    //     .filter(|x| part_ids.contains(x.1))
    //     .map(|x| x.0)
    //     .collect();
    // for (line_num, line) in input.iter().enumerate() {
    //     for (char_pos, ch) in line.char_indices() {
    //         if ch == '.' {
    //             print!("{ch}");
    //             continue;
    //         }
    //
    //         let origin: Position = (line_num, char_pos);
    //         if tracked_positions.contains(&&origin) {
    //             print!(" ");
    //         } else {
    //             print!("{ch}")
    //         }
    //     }
    //     println!();
    // }
}

fn calculate_position(origin: Position, max: Position, dy: isize, dx: isize) -> Option<Position> {
    let new_y = (origin.0 as isize) + dy;
    let new_x = (origin.1 as isize) + dx;
    if new_y >= 0 && new_y <= (max.0 as isize) {
        if new_x >= 0 && new_x <= (max.1 as isize) {
            return Some((new_y as usize, new_x as usize));
        }
    }
    return None;
}

fn try_assign_id(
    number_builder: &mut String,
    position_to_id: &mut HashMap<Position, ID>,
    id_to_number: &mut Vec<usize>,
    tracked_positions: &mut Vec<Position>,
) {
    if !number_builder.is_empty() {
        let number = number_builder.parse().unwrap();
        id_to_number.push(number);
        let id: ID = id_to_number.len() - 1;
        // println!("Assigned {id} to {number}");

        for pos in &mut *tracked_positions {
            position_to_id.insert(*pos, id);
        }

        number_builder.clear();
        tracked_positions.clear();
    }
}

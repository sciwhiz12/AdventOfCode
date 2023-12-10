use std::collections::{HashMap, HashSet};
use std::fs;

use crate::day10::Direction::{EAST, NORTH, SOUTH, WEST};

pub fn run() {
    let raw_input: String =
        fs::read_to_string("input/day10.txt").expect("Unable to read input file");
    let input: Vec<&str> = raw_input.lines().collect();

    let mut full_map: Vec<Vec<char>> = input.iter().map(|x| x.chars().collect()).collect();
    let mut start: Position = (0, 0);
    for (y, row) in full_map.iter().enumerate() {
        for (x, cell) in row.iter().enumerate() {
            if *cell == 'S' {
                start = (x, y)
            }
        }
    }

    let mut traversed_map: Vec<Vec<char>> = vec![];
    full_map.iter().for_each(|x| {
        let mut vec = vec![];
        vec.resize(x.len(), ' ');
        traversed_map.push(vec)
    });

    let start_filled = fill_character(start, &full_map).unwrap();
    let start_direction = start_filled.1;
    let start_pipe = start_filled.0;
    full_map[start.1][start.0] = start_pipe;
    traversed_map[start.1][start.0] = start_pipe;
    let full_map = full_map;

    let directions = &vec![NORTH, SOUTH, EAST, WEST];

    let mut traversed_positions: Vec<Position> = vec![];
    let mut pos_to_index: HashMap<Position, usize> = HashMap::new();
    traversed_positions.push(start);
    pos_to_index.insert(start, 0);

    // Start traversing from one side
    let mut prev_direction = start_direction;
    let mut current_position = add_direction(start, &prev_direction);

    'main: while current_position != start {
        let char_at_pos = get_character(current_position, &full_map).unwrap();
        pos_to_index.insert(current_position, traversed_positions.len());
        traversed_positions.push(current_position);
        traversed_map[current_position.1][current_position.0] = char_at_pos;

        for dir in directions {
            if *dir == prev_direction.opposite() {
                continue;
            }
            if !has_connection(char_at_pos, dir) {
                continue;
            }
            let new_pos = add_direction(current_position, &dir);
            if let Some(new_char) = get_character(new_pos, &full_map) {
                if has_connection(new_char, &dir.opposite()) {
                    current_position = new_pos;
                    prev_direction = dir.clone();
                    continue 'main;
                }
            }
        }
        panic!()
    }

    let farthest_distance = traversed_positions.len() / 2;
    println!("Farthest distance: {}", farthest_distance);

    // PART 2

    // Expand the (traversed) map by inserting space between each character, horizontally and vertically
    // Then, fill in the gaps by inserting pipes according to adjacent pieces -- for an empty space, use
    // ',' to mark it as "filled by us artificially"
    // Then, starting at a blank space outside of the loop, traverse every non-pipe neighbor (',',
    // '.', and ' ')and blank it out with '.'
    // Count the remaining blank spaces

    let real_blank = '.';
    let artificial_blank = ' ';
    let mut new_map: Vec<Vec<char>> = vec![];
    for (y, row) in traversed_map.iter().enumerate() {
        let mut row_new: Vec<char> = vec![];
        for (_x, cell) in row.iter().enumerate() {
            if *cell == ' ' {
                // Replace blank spaces with dots
                row_new.push(real_blank);
            } else {
                row_new.push(*cell);
            }
            // Check only the west side (assume top and bottom are blank)
            row_new.push(
                fill_character_0(&' ', &' ', cell)
                    .map(|x| x.0)
                    .unwrap_or(artificial_blank),
            )
        }
        new_map.push(row_new);
        // Fill new row
        row_new = vec![];
        for x in 0..row.len() {
            let current_pos: Position = (x, y);
            let north = get_character(current_pos, &traversed_map).unwrap_or(' ');
            let south =
                get_character(add_direction(current_pos, &SOUTH), &traversed_map).unwrap_or(' ');
            row_new.push(
                fill_character_0(&south, &north, &' ')
                    .map(|x| x.0)
                    .unwrap_or(artificial_blank),
            );
            row_new.push(artificial_blank);
        }
        new_map.push(row_new);
    }

    // Pad one layer outside the map
    for row in new_map.iter_mut() {
        row.insert(0, artificial_blank);
        row.push(artificial_blank);
    }
    let blank_vec = vec![artificial_blank; new_map[0].len()];
    new_map.insert(0, blank_vec.clone());
    new_map.push(blank_vec.clone());

    // Starting from the top-left, traverse every single element that can be reached (including
    // pipes and blanks) from non-pipe characters and mark them down
    let mut seen_positions: HashSet<Position> = HashSet::new();
    let mut to_process: Vec<Position> = vec![(0, 0)];
    while let Some(current_pos) = to_process.pop() {
        if seen_positions.contains(&current_pos) {
            continue;
        }
        seen_positions.insert(current_pos);
        if let Some(char) = get_character(current_pos, &new_map) {
            if !(char == real_blank || char == artificial_blank) {
                // Don't look at this character's neighbors (otherwise we might jump across a pipe!)
                continue;
            }
            to_process.push(add_direction(current_pos, &NORTH));
            to_process.push(add_direction(current_pos, &SOUTH));
            to_process.push(add_direction(current_pos, &EAST));
            to_process.push(add_direction(current_pos, &WEST));
        }
    }

    let mut inside_count = 0;
    for (y, row) in new_map.iter().enumerate() {
        for (x, cell) in row.iter().enumerate() {
            let pos: Position = (x, y);
            if !seen_positions.contains(&pos) {
                if *cell == '.' {
                    inside_count += 1;
                    // print!("I");
                } else {
                    // print!(" ");
                }
            } else {
                // print!("{}", cell);
            }
        }
        // println!();
    }
    println!("Count of blank spaces inside loop: {}", inside_count)
}

fn fill_character_0(south: &char, north: &char, west: &char) -> Option<(char, Direction)> {
    let has_south = has_connection(*south, &NORTH);
    let has_north = has_connection(*north, &SOUTH);
    let has_west = has_connection(*west, &EAST);
    return if has_north && has_south {
        Some(('|', NORTH))
    } else if !(has_north || has_south) {
        if has_west {
            Some(('-', WEST))
        } else {
            None
        }
    } else if has_north {
        if has_west {
            Some(('J', NORTH))
        } else {
            Some(('L', NORTH))
        }
    } else if has_south {
        if has_west {
            Some(('7', SOUTH))
        } else {
            Some(('F', SOUTH))
        }
    } else {
        None
    };
}

fn fill_character(start: Position, map: &Vec<Vec<char>>) -> Option<(char, Direction)> {
    let south = get_character(add_direction(start, &SOUTH), &map).unwrap_or('.');
    let north = get_character(add_direction(start, &NORTH), &map).unwrap_or('.');
    let west = get_character(add_direction(start, &WEST), &map).unwrap_or('.');
    fill_character_0(&south, &north, &west)
}

fn get_character(pos: Position, map: &Vec<Vec<char>>) -> Option<char> {
    map.get(pos.1).and_then(|row| row.get(pos.0)).cloned()
}

fn add(pos: Position, dx: isize, dy: isize) -> Position {
    (
        (pos.0 as isize + dx) as usize,
        (pos.1 as isize + dy) as usize,
    )
}

fn add_direction(pos: Position, dir: &Direction) -> Position {
    return match dir {
        NORTH => add(pos, 0, -1),
        SOUTH => add(pos, 0, 1),
        WEST => add(pos, -1, 0),
        EAST => add(pos, 1, 0),
    };
}

// (x, y)
type Position = (usize, usize);

#[derive(PartialEq, Eq, Debug, Clone, Copy)]
enum Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST,
}

impl Direction {
    fn opposite(&self) -> Direction {
        return match self {
            NORTH => SOUTH,
            SOUTH => NORTH,
            WEST => EAST,
            EAST => WEST,
        };
    }
}

fn has_connection(ch: char, incoming: &Direction) -> bool {
    let incoming = incoming.clone();
    return match ch {
        '|' => incoming == NORTH || incoming == SOUTH,
        '-' => incoming == EAST || incoming == WEST,
        'L' => incoming == NORTH || incoming == EAST,
        'J' => incoming == NORTH || incoming == WEST,
        '7' => incoming == SOUTH || incoming == WEST,
        'F' => incoming == SOUTH || incoming == EAST,
        _ => false,
    };
}

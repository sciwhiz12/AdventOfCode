use std::cmp::max;
use std::fs;

use regex::Regex;

pub fn run() {
    let raw_input: String =
        fs::read_to_string("input/day2.txt").expect("Unable to read input file");
    let input: Vec<&str> = raw_input.split("\n").filter(|x| !x.is_empty()).collect();

    let cube_regex = Regex::new(r"(\d+) (blue|green|red)").unwrap();
    let known_red_cubes = 12;
    let known_green_cubes = 13;
    let known_blue_cubes = 14;

    let mut game_id = 0;
    let mut possible_games_id_sum = 0;
    let mut power_sum = 0;

    for line in input {
        game_id += 1;
        let colon_idx = line.find(':').unwrap();
        let game_data = &line.trim()[colon_idx + 2..];

        let mut req_red_cubes = 0;
        let mut req_green_cubes = 0;
        let mut req_blue_cubes = 0;

        let mut possible = true;
        for game_set in game_data.split("; ") {
            let mut red_cubes = 0;
            let mut green_cubes = 0;
            let mut blue_cubes = 0;

            for cube_count_match in cube_regex.captures_iter(game_set) {
                let cube_count = cube_count_match
                    .get(1)
                    .unwrap()
                    .as_str()
                    .parse::<u32>()
                    .unwrap();
                match cube_count_match.get(2).unwrap().as_str() {
                    "red" => red_cubes = cube_count,
                    "green" => green_cubes = cube_count,
                    "blue" => blue_cubes = cube_count,
                    _ => panic!("Unknown cube color"),
                }
            }

            req_red_cubes = max(req_red_cubes, red_cubes);
            req_blue_cubes = max(req_blue_cubes, blue_cubes);
            req_green_cubes = max(req_green_cubes, green_cubes);

            if red_cubes > known_red_cubes
                || blue_cubes > known_blue_cubes
                || green_cubes > known_green_cubes
            {
                possible = false;
            }
        }

        let power = req_red_cubes * req_blue_cubes * req_green_cubes;
        power_sum += power;

        if possible {
            possible_games_id_sum += game_id;
        }
    }

    println!("Sum of IDs for impossible games: {}", possible_games_id_sum);
    println!("Sum of powers of minimum sets: {}", power_sum);
}

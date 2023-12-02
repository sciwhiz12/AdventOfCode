use std::fs;

use regex::*;

pub fn run() {
    let raw_input: String =
        fs::read_to_string("input/day1.txt").expect("Unable to read input file");
    let input: Vec<&str> = raw_input.split("\n").filter(|x| !x.is_empty()).collect();

    // PART 1

    let mut calibration_sum: u32 = 0;
    for line in &input {
        let first_digit = line
            .chars()
            .find(|ch| ch.is_numeric())
            .expect("Did not find number");
        let last_digit = line
            .chars()
            .rev()
            .find(|ch| ch.is_numeric())
            .expect("Did not find number");

        let calibration_value: String =
            first_digit.to_string().as_str().to_owned() + last_digit.to_string().as_str();

        calibration_sum += calibration_value.parse::<u32>().expect("What?!")
    }
    println!("Sum of all calibration values (part 1): {calibration_sum}");

    // PART 2

    let forward_regex = &Regex::new(r"\d|one|two|three|four|five|six|seven|eight|nine").unwrap();
    let reverse_regex = &Regex::new(r"\d|eno|owt|eerht|ruof|evif|xis|neves|thgie|enin").unwrap();
    let mut calibration_sum: u32 = 0;
    for line in &input {
        let line = line.trim();

        let first_digit = convert(
            forward_regex
                .captures_iter(line)
                .next()
                .unwrap()
                .get(0)
                .unwrap()
                .as_str(),
        )
        .unwrap();
        let reverse = line.chars().rev().collect::<String>();
        let last_digit_reversed = reverse_regex
            .captures_iter(&reverse)
            .next()
            .unwrap()
            .get(0)
            .unwrap()
            .as_str();
        let last_digit = last_digit_reversed.chars().rev().collect::<String>();
        let last_digit = convert(last_digit.as_str()).unwrap();

        let calibration_value: String =
            first_digit.to_string().as_str().to_owned() + last_digit.to_string().as_str();

        calibration_sum += calibration_value.parse::<u32>().unwrap()
    }
    println!("Sum of all calibration values (part 2): {calibration_sum}");
}

fn convert(val: &str) -> Option<&str> {
    return match val {
        "0" | "zero" => Some("0"),
        "1" | "one" => Some("1"),
        "2" | "two" => Some("2"),
        "3" | "three" => Some("3"),
        "4" | "four" => Some("4"),
        "5" | "five" => Some("5"),
        "6" | "six" => Some("6"),
        "7" | "seven" => Some("7"),
        "8" | "eight" => Some("8"),
        "9" | "nine" => Some("9"),
        _ => None,
    };
}

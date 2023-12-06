use std::fs;

pub fn run() {
    let raw_input: String =
        fs::read_to_string("input/day6.txt").expect("Unable to read input file");
    let input: Vec<&str> = raw_input.lines().collect();

    let time_line = input.get(0).unwrap();
    let distance_line = input.get(1).unwrap();

    // Don't forget to the headers
    let mut time_iter = time_line.split_whitespace().skip(1);
    let mut dist_iter = distance_line.split_whitespace().skip(1);

    let mut time_to_distance: Vec<(u64, u64)> = vec![];

    while let Some(time) = time_iter.next() {
        let distance = dist_iter.next().unwrap();

        time_to_distance.push((time.parse().unwrap(), distance.parse().unwrap()));
    }

    // Part 1
    let mut multiplicative_wins = 1;
    for (time, distance) in time_to_distance {
        let mut wins = 0;
        for speed in 1..time {
            // At speed X, the distance traveled is (time - X) * X
            let traveled_distance = (time - speed) * speed;
            if traveled_distance > distance {
                wins += 1;
            }
        }
        multiplicative_wins *= wins;
    }
    println!("Product of ways to win for each race: {multiplicative_wins}");

    // Part 2
    let full_time = remove_whitespace_and_parse(time_line);
    let full_distance = remove_whitespace_and_parse(distance_line);

    let mut wins = 0;
    for speed in 1..full_time {
        // At speed X, the distance traveled is (time - X) * X
        let traveled_distance = (full_time - speed) * speed;
        if traveled_distance > full_distance {
            wins += 1;
        }
    }
    println!("Wins for full race: {wins}");
}

fn remove_whitespace_and_parse(input: &str) -> u64 {
    input
        .split_once(":")
        .unwrap()
        .1
        .chars()
        .filter(|x| x.is_numeric())
        .collect::<String>()
        .parse()
        .unwrap()
}

use std::collections::HashMap;
use std::fs;
use std::hash::Hash;

use itertools::Itertools;

pub fn run() {
    let raw_input: String =
        fs::read_to_string("input/day8.txt").expect("Unable to read input file");
    let input: Vec<&str> = raw_input.lines().collect();
    let mut iter = input.iter();

    let instructions = iter.next().cloned().unwrap();

    let mut nodes: HashMap<&str, (&str, &str)> = HashMap::new();
    iter.next();
    for line in iter {
        let split = line.split(' ').collect_vec();
        let start = split[0];
        let left = &split[2][1..4];
        let right = &split[3][0..3];

        nodes.insert(start, (left, right));
    }
    let nodes = &nodes;

    // PART 1

    let mut followed_instructions = calculate_path(nodes, instructions, "AAA", |x| x == "ZZZ");
    println!(
        "Steps required to reach ZZZ: {}",
        followed_instructions.1.len()
    );

    // PART 2

    let start_nodes = nodes
        .keys()
        .filter(|x| x.ends_with("A"))
        .cloned()
        .collect_vec();
    let end_nodes = nodes
        .keys()
        .filter(|x| x.ends_with("Z"))
        .cloned()
        .collect_vec();
    println!("begin: {:?}", start_nodes);
    println!("must: {:?}", end_nodes);

    let mut lcm: u128 = 1;

    for start in start_nodes {
        let result = calculate_path(nodes, instructions, start, |x| x.ends_with("Z"));

        let steps = result.1.len() as u128;
        println!("Took {} steps from {} to {}", steps, start, result.0);
        lcm = num::integer::lcm(lcm, steps)
    }
    println!("Steps needed to reach final state: {}", lcm);
}

fn calculate_path(
    nodes: &HashMap<&str, (&str, &str)>,
    instructions: &str,
    start: &str,
    end: fn(&str) -> bool,
) -> (String, String) {
    let mut node = start;
    let mut followed_instructions = String::new();
    'main: loop {
        for inst in instructions.chars() {
            followed_instructions.push(inst);
            let node_ways = nodes.get(node).unwrap();
            let next_node = match inst {
                'L' => node_ways.0,
                'R' => node_ways.1,
                _ => panic!("What?!"),
            };
            node = next_node;
            // println!("{} + {} => {} ({})", node, inst, next_node, followed_instructions);

            if end(node) {
                break 'main;
            }
        }
    }

    return (String::from(node), followed_instructions);
}

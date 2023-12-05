use std::cmp::min;
use std::collections::BTreeMap;
use std::fmt::{Debug, Display, Formatter};
use std::fs;
use std::slice::Iter;

pub fn run() {
    let raw_input: String =
        fs::read_to_string("input/day5.txt").expect("Unable to read input file");
    let input: Vec<&str> = raw_input.lines().collect();

    let mut iter = input.iter();

    // Parse the seeds
    let seeds: Vec<u64> = iter
        .next()
        .unwrap()
        .split_once(":")
        .unwrap()
        .1
        .split_whitespace()
        .map(|x| x.parse().unwrap())
        .collect();
    iter.next(); // Consume empty separator line

    // Parse each map
    let seed_to_soil = create_map(&mut iter);
    let soil_to_fertilizer = create_map(&mut iter);
    let fertilizer_to_water = create_map(&mut iter);
    let water_to_light = create_map(&mut iter);
    let light_to_temperature = create_map(&mut iter);
    let temperature_to_humidity = create_map(&mut iter);
    let humidity_to_location = create_map(&mut iter);
    drop(iter);
    let seeds = &seeds;

    let map_seed_to_location = move |seed| {
        let soil = map_value(&seed_to_soil, seed);
        let fertilizer = map_value(&soil_to_fertilizer, soil);
        let water = map_value(&fertilizer_to_water, fertilizer);
        let light = map_value(&water_to_light, water);
        let temperature = map_value(&light_to_temperature, light);
        let humidity = map_value(&temperature_to_humidity, temperature);
        let location = map_value(&humidity_to_location, humidity);

        // println!("            {seed} => [{soil}, {fertilizer}, {water}, {light}, {temperature}, {humidity}] => {location}");
        return location;
    };

    //
    // for seed in 0..100 {
    //     // let old = map_value(&seed_to_soil, seed);
    //     // let new = map_value(&base_seed_to_soil, seed);
    //     // print!("{seed} => {old} / {new}");
    //     // if old != new {
    //     //     print!(" [!]");
    //     // }
    //     // println!();
    // }
    //
    let mut lowest_location = u64::MAX;
    for seed in seeds {
        let location = map_seed_to_location(seed.clone());
        lowest_location = min(lowest_location, location);
    }
    println!("Lowest location number (part 1): {lowest_location}");

    // PART 2
    // let seed_pairs: Vec<&[u64]> = seeds.chunks(2).collect();
    //
    // lowest_location = u64::MAX;
    // for slice in seed_pairs {
    //     let seed_base = slice[0];
    //     let length = slice[1];
    //
    //     for seed in (seed_base - 1)..(seed_base + length) {
    //         let location = map_seed_to_location(seed);
    //         lowest_location = min(lowest_location, location);
    //     }
    // }
    // print!("Lowest location number (part 2): {lowest_location}");

    let mut known_seeds: BTreeMap<u64, Entry> = BTreeMap::new();

    let mut lowest_known = u64::MAX;
    for seed in seeds.chunks(2) {
        let start = seed[0];
        let length = seed[1];
        known_seeds.insert(
            start,
            Entry {
                input_base: start,
                output_base: start,
                length,
            },
        );

        // BRUTEFORCE?
        let mut local_lowest_known = u64::MAX;
        println!("{:?}: start", start..start + length);
        for seed in start..start + length {
            local_lowest_known = min(local_lowest_known, map_seed_to_location(seed.clone()));
        }
        println!(
            "{:?}: end, found {}",
            start..start + length,
            local_lowest_known
        );
        lowest_known = min(lowest_known, local_lowest_known);
    }
    println!("Lowest known location: {}", lowest_known);

    return;

    // let seed_to_soil = enhance_map(&seed_to_soil);
    // let soil_to_fertilizer = enhance_map(&soil_to_fertilizer);
    // let fertilizer_to_water = enhance_map(&fertilizer_to_water);
    // let water_to_light = enhance_map(&water_to_light);
    // let light_to_temperature = enhance_map(&light_to_temperature);
    // let temperature_to_humidity = enhance_map(&temperature_to_humidity);
    // let humidity_to_location = enhance_map(&humidity_to_location);
    //
    // let mut candidates: Vec<Range<u64>> = Vec::new();
    //
    // println!("{:?}", seed_to_soil);
    // for (_, seed) in known_seeds {
    //     let range = seed.out_range();
    //     println!("{range:?}");
    //     for (_, seed2soil) in seed_to_soil.range(..seed.out_range().end) {
    //         let range = range.constrain_and_map(seed2soil);
    //         for (_, soil2fertilizer) in soil_to_fertilizer.range(..seed2soil.out_range().end) {
    //             let range = range.constrain_and_map(soil2fertilizer);
    //             for (_, fertilizer2water) in fertilizer_to_water.range(..soil2fertilizer.out_range().end) {
    //                 let range = range.constrain_and_map(fertilizer2water);
    //                 for (_, water2light) in water_to_light.range(..fertilizer2water.out_range().end) {
    //                     let range = range.constrain_and_map(water2light);
    //                     for (_, light2temperature) in light_to_temperature.range(..water2light.out_range().end) {
    //                         let range = range.constrain_and_map(light2temperature);
    //                         for (_, temperature2humidity) in temperature_to_humidity.range(..light2temperature.out_range().end) {
    //                             let range = range.constrain_and_map(temperature2humidity);
    //                             'h2l: for (_, humidity2location) in humidity_to_location.range(..temperature2humidity.out_range().end) {
    //                                 let range = range.constrain_and_map(humidity2location);
    //
    //                                 if range.end >= (u64::MAX >> 8) || range.start == 0 {
    //                                     continue;
    //                                 }
    //                                 let mut contained: bool = false;
    //                                 let mut remove: Vec<usize> = vec![];
    //                                 for (idx, x) in candidates.iter().enumerate() {
    //                                     if x.fully_contains(&range) {
    //                                         contained = true;
    //                                     } else if range.fully_contains(x) {
    //                                         remove.push(idx);
    //                                     }
    //                                 }
    //                                 if !contained {
    //                                     println!("{range:?}");
    //                                     &mut candidates.push(range);
    //                                 }
    //                                 remove.iter().rev().for_each(|x| { &mut candidates.remove(*x); });
    //                             }
    //                         }
    //                     }
    //                 }
    //             }
    //         }
    //     }
    // }
    //
    // let mut lowest = u64::MAX;
    // for x in candidates {
    //     println!("({}, {})", x.start, x.end);
    //     lowest = min(lowest, x.start);
    // }
    // println!("Lowest start: {}", lowest)
}

// fn enhance_map(map: &BTreeMap<u64, Entry>) -> BTreeMap<u64, Entry> {
//     let mut result = map.clone();
//
//     for (_, entry) in map {
//         result.insert(entry.input_base, *entry);
//         result.insert(entry.input_base + entry.length - 1, *entry);
//     }
//
//     result
// }

// fn overlaps(a: &Entry, b: &Entry) -> bool {
//     let a_end = a.input_base + a.length;
//     let b_end = b.input_base + b.length;
//
//     return a_end > b.input_base || b_end > a.input_base;
// }

// fn contained_within(entry: &Entry, value: u64, key_func: fn(&Entry) -> u64) -> bool {
//     let key = key_func(entry);
//     value >= key && value < (key + entry.length)
// }

// fn calculate_value(entry: &Entry, input: u64) -> Option<u64> {
//     if input >= entry.input_base && input < (entry.input_base + entry.length) {
//         let diff: i64 = input as i64 - entry.input_base as i64;
//         Some((entry.output_base as i64 + diff) as u64);
//     }
//     None
// }

fn map_value(map: &BTreeMap<u64, Entry>, input: u64) -> u64 {
    // Find the key in the map with the lowest value (closer to 0) nearest to the input
    // Check if the input is within the range prescribed by that key + length
    // If yes, then calculate difference between key and input, and add to the output base
    // If not, then pass input as output

    let iter = map.range(..input + 1);
    if let Some((_, closest)) = iter.last() {
        let key = closest.input_base;
        let range_end = key + closest.length;

        if range_end >= input {
            let diff: i128 = input as i128 - key as i128;
            let output = (closest.output_base as i128 + diff) as u64;
            return output;
        }
    }

    return input;
}

#[derive(Clone, Copy)]
struct Entry {
    input_base: u64,
    output_base: u64,
    length: u64,
}

// impl Entry {
//     fn out_range(&self) -> Range<u64> {
//         return self.output_base..self.output_base + self.length;
//     }
//
//     fn map_value(&self, value: u64) -> u64 {
//         let offset: i128 = value as i128 - self.input_base as i128;
//         if offset < 0 {
//             panic!("offset is negative: {} ({:?}; {})", offset, self, value)
//         }
//         return (self.output_base as i128 + offset) as u64;
//     }
// }

// trait EntryConstrain {
//     fn constrain_and_map(&self, entry: &Entry) -> Self;
//
//     fn fully_contains(&self, other: &Self) -> bool;
// }

// impl EntryConstrain for Range<u64> {
//     fn constrain_and_map(&self, entry: &Entry) -> Self {
//         let bounds = entry.out_range();
//
//         let offset: i128 = entry.output_base as i128 - entry.input_base as i128;
//
//         Range {
//             start: max(self.start as i128 + offset, bounds.start as i128 + offset) as u64,
//             end: min(self.end as i128 + offset, bounds.end as i128 + offset) as u64,
//         }
//     }
//
//     fn fully_contains(&self, other: &Self) -> bool {
//         self.start <= other.start && self.end >= other.end
//     }
// }

impl Display for Entry {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "{} => ({}, {})",
            self.input_base, self.output_base, self.length
        )
    }
}

impl Debug for Entry {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "{} => ({}, {})",
            self.input_base, self.output_base, self.length
        )
    }
}

fn create_map(iter: &mut Iter<&str>) -> BTreeMap<u64, Entry> {
    // Assume one line for header, as many lines for contents, and one empty separator line

    iter.next(); // Header

    let mut map = BTreeMap::new();
    while let Some(line) = iter.next() {
        // Read until empty line
        if line.is_empty() {
            break;
        }

        // <after> <before> <length>
        let split: Vec<&str> = line.split_whitespace().collect();

        let before: u64 = split[1].parse().unwrap();
        let after: u64 = split[0].parse().unwrap();
        let length: u64 = split[2].parse().unwrap();

        map.insert(
            before,
            Entry {
                input_base: before,
                output_base: after,
                length,
            },
        );
    }

    return map;
}

// fn fill_gaps(map: &mut BTreeMap<u64, Entry>) {
//     // Fill gaps by creating dummy entries
//     let mut to_add: Vec<Entry> = vec![];
//
//     let mut last_end = 0;
//     for entry in map.values() {
//         if last_end < entry.input_base {
//             // Discontinuity
//             let gap_size = entry.input_base - last_end;
//             to_add.push(Entry {
//                 input_base: last_end,
//                 output_base: last_end,
//                 length: gap_size,
//             });
//         }
//         last_end = entry.input_base + entry.length;
//     }
//     // Insert up to very end
//     to_add.push(Entry {
//         input_base: last_end,
//         output_base: last_end,
//         length: u64::MAX - 1 - last_end,
//     });
//
//     for entry in to_add {
//         map.insert(entry.input_base, entry);
//     }
// }

// fn recreate_map(map: &BTreeMap<u64, Entry>, key_func: fn(&Entry) -> u64) -> BTreeMap<u64, Entry> {
//     let mut result = BTreeMap::new();
//
//     for (_, entry) in map {
//         result.insert(key_func(entry), Entry {
//             input_base: entry.output_base,
//             output_base: entry.input_base,
//             length: entry.length,
//         });
//     }
//
//     return result;
// }

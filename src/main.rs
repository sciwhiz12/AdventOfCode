mod day1;
mod day2;
mod day3;

fn main() {
    let day: u32 = 3;

    match day {
        1 => day1::run(),
        2 => day2::run(),
        3 => day3::run(),
        _ => {}
    }
}

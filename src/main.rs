mod day1;
mod day2;

fn main() {
    let day: u32 = 2;

    match day {
        1 => day1::run(),
        2 => day2::run(),
        _ => {}
    }
}

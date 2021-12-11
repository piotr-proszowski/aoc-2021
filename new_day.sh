#/bin/sh
set -e

DAY=$1
if [ -z "$DAY" ]
then
  echo "You have to provide which day we have today!"
  exit 1
fi

echo "Today is a day number $DAY"

echo "Pulling repo"
git pull
echo "DONE!"

echo "Creating directories..."
mkdir -p src/test/resources/advent-of-code/day$DAY
mkdir -p src/main/kotlin/eu/proszkie/adventofcode/day$DAY
mkdir -p src/test/groovy/eu/proszkie/adventofcode/day$DAY
echo "DONE!"

echo "Fetching input..."

curl "https://adventofcode.com/2021/day/$DAY/input" -H "cookie: session=$AOC_SESSION;"  > src/test/resources/advent-of-code/day$DAY/input2

echo "DONE!"

echo "Good luck!"

echo "Opening IDE"
/Applications/IntelliJ\ IDEA.app/Contents/MacOS/idea .&

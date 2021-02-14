cd "/home/david/Thesis/FaaS-GraalVM" || exit

echo "Fetching changes"

git checkout sync

git fetch
git pull

echo "Adding changes"

git add ./*

echo "Commiting"

git commit -m "Auto Commit ($(printf '%(%H:%M_%Y-%m-%d)T\n' -1))"

echo "Pushing"

git push

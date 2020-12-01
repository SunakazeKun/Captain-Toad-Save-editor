import csv
import struct

node_types = [
    "Season",
    "Chapter",
    "Stage",
    "HideStage"
]

node_icons = [
    "Default",
    "Season",
    "Chapter",
    "Star",
    "Shine",
    "Crown",
    "ToadBrigade",
    "GhostPlayer",
    "GhostPlayerMaze",
    "Bonus"
]

stage_types = [
    "Normal",
    "Prologue",
    "Illustration",
    "IllustrationDemo",
    "Bonus",
    "MiniGame",
    "TrickArt",
    "Special3DWorld",
    "SpecialCherry",
    "SpecialManek",
    "SpecialPrologue",
    "SpecialBonus",
    "SpecialOther",
    "Labyrinth"
]

strings = []
entries = []
out = bytearray(16)
out_strings = bytearray()
out_data = bytearray()

with open("src/assets/bin/LevelNodeInfo.csv", encoding="utf-8") as f:
    reader = csv.DictReader(f)

    for entry in reader:
        if entry["StageName"] not in strings:
            strings.append(entry["StageName"])
        entries.append(entry)

strings.sort()

for string in strings:
    encoded = string.encode("utf-8")
    out_strings += bytearray(struct.pack(">H", len(encoded)))
    out_strings += bytearray(encoded)

num_entries = 0

for entry in entries:
    name_idx = strings.index(entry["StageName"])
    id = int(entry["CourseId"])
    type = stage_types.index(entry["StageType"]) if entry["StageType"] in stage_types else -1
    node_type = node_types.index(entry["NodeType"])
    node_icon = node_icons.index(entry["NodeIcon"])
    node_info = node_type | (node_icon << 4)
    version = int(entry["GameVersion"])
    time = int(entry["ChallengeTime"])
    has_dot_kinopio = entry["HasDotKinopio"] == "true"
    has_collect_item = entry["HasCollectItem"] == "true"
    has_badge_condition = entry["HasBadgeCondition"] == "true"
    has_challenge_time = entry["HasChallengeTime"] == "true"
    has_dlc_collect_item = entry["HasDlcCollectItem"] == "true"
    has_preview_image = entry["HasPreviewImage"] == "true"
    flags = 1 if has_dot_kinopio else 0
    flags |= 2 if has_collect_item else 0
    flags |= 4 if has_badge_condition else 0
    flags |= 8 if has_challenge_time else 0
    flags |= 16 if has_dlc_collect_item else 0
    flags |= 32 if has_preview_image else 0

    out_entry = struct.pack(">HhbBBBH", name_idx, id, type, node_info, version, flags, time)
    out_data += bytearray(out_entry)
    num_entries += 1

out += out_strings + out_data
struct.pack_into(">IIII", out, 0, num_entries, len(strings), len(out_strings), len(out))

with open("src/assets/bin/LevelNodeInfo.bin", "wb") as f:
    f.write(out)
    f.flush()

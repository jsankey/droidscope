#! /usr/bin/env bash

pidfile="$1"
pidprop="$2"
command="$3"
shift 3

"$command" "$@" &
echo "$pidprop=$!" > "$pidfile"

exit 0

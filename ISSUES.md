# Issues

- [x] Should immediately fetch tickets on vehicle added
- [x] New tag renders vertically when squeezed
- [x] Pull-down refreshing indicator renders behind and left rather than upfront and center (towards top)
- [x] The bottom banner appears as a navigational overlay but disappears outside of parking tickets screen
- [x] List items (i.e., tickets) do not reach all the way to bottom banner
- [x] Rename "my vehicles" title to "vehicles"
- [x] App seems to only be pulling (or displaying) tickets till 12/31/25 (verified: NYC Open Data has 2026 tickets - the specific vehicle just has no newer tickets)
- [x] The tickets page is ordering by month, day, then year rather than actual date
- [x] Move to dark mode only, fixed theme using colors in: https://m3.material.io/
- [x] Map ticket types into nice, readable types for display
- [x] Replace the logo with a generic ticket logo until something better can be found
- [x] App should fetch tickets on startup (not just on pull-to-refresh)
- [x] Add click-through button to view summons image on NYC portal
- [x] Combine date+time into single issueDateTime field (ISO in DB, LocalDateTime in domain)
- [x] For the state dropdown, just display long names, not initials
- [x] Default to "unspecified" in vehicle type dropdown
- [x] On open, app displays welcome screen for split second before going to tickets page. Fix it.
- [x] Find ways to incorporate more of the accent colo/r
- [x] Ability to filter on paid/unpaid, with older unpaid hidden under "show older"
- [x] Total amount owed
- [x] On the Tickets page, "show x older unpaid" should be displayed below the tickets
- [x] Find a less vibrant blue for the buttons
- [x] The "view violations" button does not need a border
- [ ] Remove the pay now button (we can add that functionality later) and replace it with the view violations button
- [ ] Give slightly more space in the tickets page between total amount owed card and the tickets list

## Stretch goals

- [ ] Map view
- [ ] Map clustering
- [ ] Analyze past ticket in cluster area, display predicted inspection time
- [ ] Save parking location. Can inquire which side of street for accuracy. Will then warn when alternate side tomorrow and alert. Will also say if alternate side suspended.


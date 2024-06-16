# 

- supported fields (nesting allowed, but have to be inside a `<form>` tag):
  - [x] `<input>`:
    - [x] `type="checkbox"`
    - [x] `type="date"`
    - [x] `type="datetime-local"`
    - [x] `type="email"`
    - [x] `type="file"`
    - [x] `type="hidden"`
    - [x] `type="month"`
    - [x] `type="number"`
    - [x] `type="password"`
    - [x] `type="radio"`
    - [x] `type="reset"`
    - [x] `type="tel"`
    - [x] `type="text"`
    - [x] `type="time"`
    - [x] `type="url"`
    - [ ] `type="week"` 
  - [x] `<select>` (`optgroup`s don't work in pdf, but children will be rendered as options)
  - [x] `<textarea>`
  - [x] `<signature name="...">` (custom field)
  - [x] `<fieldset>` (grouping with legend)


- new attribute: `format` for `date`, `datetime-local`, `month` and `time` input types, defaults:
    - `date`: `dd.mm.yyyy`
    - `datetime-local`: `dd.mm.yyyy HH:MM`
    - `month`: `mmm yyyy`
    - `time`: `HH:MM`
    - when `format` is set, the value will be formatted according to the format string


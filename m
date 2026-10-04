<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Smart Home Automation Simulator — Class Diagram</title>
  <script src="https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.min.js"></script>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet" />
  <style>
    :root {
      --bg-deep:    #0d1117;
      --bg-base:    #161b22;
      --bg-surface: #1c2230;
      --bg-card:    #21293a;
      --bg-raised:  #2a3347;
      --border:     #2d3748;
      --border-hi:  #3d4f6b;
      --accent:     #3dd6c0;
      --accent-dim: rgba(61,214,192,.12);
      --accent2:    #6ea8e8;
      --accent3:    #a78bfa;
      --success:    #4cc97a;
      --warning:    #e8ae3d;
      --danger:     #e86a6a;
      --text-1:     #e8ecf1;
      --text-2:     #9ba7b4;
      --text-3:     #6b7684;
      --radius:     12px;
      --shadow:     0 4px 32px rgba(0,0,0,.55);
    }

    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

    body {
      font-family: 'Inter', sans-serif;
      background: var(--bg-deep);
      color: var(--text-1);
      min-height: 100vh;
    }

    /* ── HERO HEADER ── */
    header {
      background: linear-gradient(135deg, #0d1117 0%, #131e30 40%, #0d1a25 100%);
      border-bottom: 1px solid var(--border);
      padding: 48px 40px 36px;
      position: relative;
      overflow: hidden;
    }
    header::before {
      content: '';
      position: absolute;
      top: -80px; right: -80px;
      width: 400px; height: 400px;
      background: radial-gradient(circle, rgba(61,214,192,.10) 0%, transparent 70%);
      pointer-events: none;
    }
    header::after {
      content: '';
      position: absolute;
      bottom: -60px; left: 20%;
      width: 300px; height: 300px;
      background: radial-gradient(circle, rgba(110,168,232,.07) 0%, transparent 70%);
      pointer-events: none;
    }
    .header-badge {
      display: inline-flex; align-items: center; gap: 6px;
      background: var(--accent-dim);
      border: 1px solid rgba(61,214,192,.3);
      color: var(--accent);
      font-size: 11px; font-weight: 600; letter-spacing: .08em;
      text-transform: uppercase;
      padding: 4px 12px; border-radius: 20px;
      margin-bottom: 14px;
    }
    header h1 {
      font-size: clamp(24px, 4vw, 38px);
      font-weight: 800;
      background: linear-gradient(135deg, #e8ecf1 30%, #3dd6c0 100%);
      -webkit-background-clip: text; -webkit-text-fill-color: transparent;
      background-clip: text;
      line-height: 1.2;
      margin-bottom: 10px;
    }
    header p {
      color: var(--text-2); font-size: 15px; max-width: 620px; line-height: 1.6;
    }
    .stats-row {
      display: flex; flex-wrap: wrap; gap: 12px; margin-top: 28px;
    }
    .stat-chip {
      display: flex; align-items: center; gap: 8px;
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: 8px; padding: 8px 16px;
      font-size: 13px;
    }
    .stat-chip .num { font-weight: 700; color: var(--accent); font-size: 15px; }
    .stat-chip .lbl { color: var(--text-2); }

    /* ── MAIN LAYOUT ── */
    main { max-width: 1600px; margin: 0 auto; padding: 36px 32px 80px; }

    /* ── SECTION TITLE ── */
    .section-title {
      display: flex; align-items: center; gap: 12px;
      font-size: 13px; font-weight: 600; letter-spacing: .08em;
      text-transform: uppercase; color: var(--text-3);
      margin: 48px 0 20px;
    }
    .section-title::after {
      content: ''; flex: 1; height: 1px; background: var(--border);
    }

    /* ── PACKAGE CARDS ── */
    .pkg-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 16px; margin-bottom: 48px;
    }
    .pkg-card {
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      padding: 20px;
      transition: border-color .2s, transform .2s;
      position: relative; overflow: hidden;
    }
    .pkg-card::before {
      content: ''; position: absolute;
      inset: 0; border-radius: inherit;
      background: linear-gradient(135deg, var(--pkg-color, var(--accent)) 0%, transparent 60%);
      opacity: .04; pointer-events: none;
    }
    .pkg-card:hover { border-color: var(--border-hi); transform: translateY(-2px); }
    .pkg-card .pkg-icon {
      width: 36px; height: 36px;
      background: var(--accent-dim);
      border-radius: 8px;
      display: flex; align-items: center; justify-content: center;
      font-size: 18px; margin-bottom: 12px;
    }
    .pkg-card h3 {
      font-family: 'JetBrains Mono', monospace;
      font-size: 12px; font-weight: 500;
      color: var(--accent); margin-bottom: 6px;
    }
    .pkg-card p { font-size: 13px; color: var(--text-2); line-height: 1.5; }
    .pkg-card .class-list {
      margin-top: 12px; display: flex; flex-wrap: wrap; gap: 6px;
    }
    .cls-tag {
      font-family: 'JetBrains Mono', monospace;
      font-size: 10px; font-weight: 500;
      background: var(--bg-raised);
      border: 1px solid var(--border);
      border-radius: 4px; padding: 2px 8px;
      color: var(--text-2);
    }
    .cls-tag.abstract { color: var(--warning); border-color: rgba(232,174,61,.25); }
    .cls-tag.iface    { color: var(--accent2); border-color: rgba(110,168,232,.25); }
    .cls-tag.enum     { color: var(--accent3); border-color: rgba(167,139,250,.25); }

    /* ── DIAGRAM PANEL ── */
    .diagram-panel {
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      overflow: hidden;
      box-shadow: var(--shadow);
    }
    .diagram-toolbar {
      display: flex; align-items: center; justify-content: space-between;
      padding: 14px 20px;
      background: var(--bg-surface);
      border-bottom: 1px solid var(--border);
    }
    .diagram-toolbar .toolbar-title {
      font-size: 13px; font-weight: 600; color: var(--text-1);
      display: flex; align-items: center; gap: 8px;
    }
    .dot { width: 8px; height: 8px; border-radius: 50%; }
    .dot.r { background: #ff5f57; }
    .dot.y { background: #ffbd2e; }
    .dot.g { background: #28c840; }
    .toolbar-dots { display: flex; gap: 6px; align-items: center; }
    .zoom-btn {
      background: var(--bg-raised); border: 1px solid var(--border);
      color: var(--text-2); border-radius: 6px; padding: 5px 14px;
      font-size: 12px; cursor: pointer; font-family: inherit;
      transition: background .15s, color .15s;
    }
    .zoom-btn:hover { background: var(--border-hi); color: var(--text-1); }
    .diagram-body {
      overflow: auto;
      padding: 32px;
      min-height: 400px;
      background:
        radial-gradient(ellipse at 20% 20%, rgba(61,214,192,.04) 0%, transparent 50%),
        radial-gradient(ellipse at 80% 80%, rgba(110,168,232,.04) 0%, transparent 50%),
        var(--bg-deep);
    }
    .diagram-body svg { max-width: 100%; display: block; margin: 0 auto; }
    #mermaid-wrap { transform-origin: top center; transition: transform .2s; }

    /* ── DESIGN PATTERNS TABLE ── */
    .table-wrap {
      overflow-x: auto;
      border: 1px solid var(--border);
      border-radius: var(--radius);
    }
    table {
      width: 100%; border-collapse: collapse;
      font-size: 13px;
    }
    thead tr { background: var(--bg-surface); }
    thead th {
      text-align: left; padding: 14px 18px;
      font-size: 11px; font-weight: 600; letter-spacing: .08em;
      text-transform: uppercase; color: var(--text-3);
      border-bottom: 1px solid var(--border);
    }
    tbody tr { border-bottom: 1px solid var(--border); transition: background .15s; }
    tbody tr:last-child { border-bottom: none; }
    tbody tr:hover { background: var(--bg-surface); }
    tbody td { padding: 13px 18px; vertical-align: top; }
    .pattern-badge {
      display: inline-block;
      font-weight: 600; font-size: 12px;
      padding: 3px 10px; border-radius: 6px; white-space: nowrap;
    }
    .p-strategy { background: rgba(61,214,192,.12); color: var(--accent); }
    .p-observer  { background: rgba(110,168,232,.12); color: var(--accent2); }
    .p-template  { background: rgba(232,174,61,.12); color: var(--warning); }
    .p-composite { background: rgba(167,139,250,.12); color: var(--accent3); }
    .p-singleton { background: rgba(76,201,122,.12); color: var(--success); }
    .p-inherit   { background: rgba(232,106,106,.12); color: var(--danger); }
    td code {
      font-family: 'JetBrains Mono', monospace;
      font-size: 11px; background: var(--bg-raised);
      padding: 1px 6px; border-radius: 4px; color: var(--accent);
    }

    /* ── RELATIONSHIP LEGEND ── */
    .legend {
      display: flex; flex-wrap: wrap; gap: 20px;
      padding: 20px 24px;
      background: var(--bg-surface);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      margin-top: 24px;
    }
    .legend-item {
      display: flex; align-items: center; gap: 10px;
      font-size: 12px; color: var(--text-2);
    }
    .legend-line {
      width: 36px; height: 2px;
      border-radius: 1px;
    }
    .legend-line.solid   { background: var(--accent); }
    .legend-line.dashed  { background: repeating-linear-gradient(90deg, var(--accent2) 0 6px, transparent 6px 10px); }
    .legend-line.dotted  { background: repeating-linear-gradient(90deg, var(--text-3) 0 3px, transparent 3px 7px); }
    .legend-line.compose { background: var(--accent3); }

    /* ── FOOTER ── */
    footer {
      text-align: center; padding: 28px;
      font-size: 12px; color: var(--text-3);
      border-top: 1px solid var(--border);
    }
    footer a { color: var(--accent); text-decoration: none; }

    /* PROJECT REPORT COVERAGE */
    .doc-grid {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 16px;
      margin-bottom: 24px;
    }
    .doc-card {
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      padding: 22px;
    }
    .doc-card.wide { grid-column: 1 / -1; }
    .doc-card h3 {
      display: flex; align-items: center; gap: 8px;
      color: var(--text-1); font-size: 15px; margin-bottom: 10px;
    }
    .doc-card p, .doc-card li {
      color: var(--text-2); font-size: 13px; line-height: 1.65;
    }
    .doc-card ul, .doc-card ol { padding-left: 20px; }
    .doc-card li + li { margin-top: 5px; }
    .doc-card strong { color: var(--text-1); }
    .doc-card code { color: var(--accent); }
    .eyebrow {
      display: inline-block;
      color: var(--accent); font-family: 'JetBrains Mono', monospace;
      font-size: 10px; font-weight: 600; letter-spacing: .08em;
      text-transform: uppercase; margin-bottom: 8px;
    }
    .flow-list {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(145px, 1fr));
      gap: 10px;
      padding: 0; list-style: none;
      counter-reset: flow;
    }
    .flow-list li {
      position: relative;
      min-height: 66px;
      padding: 12px 12px 12px 42px;
      background: var(--bg-surface);
      border: 1px solid var(--border);
      border-radius: 8px;
    }
    .flow-list li::before {
      counter-increment: flow;
      content: counter(flow);
      position: absolute; left: 12px; top: 12px;
      width: 21px; height: 21px; border-radius: 50%;
      display: grid; place-items: center;
      background: var(--accent-dim); color: var(--accent);
      font: 600 11px 'JetBrains Mono', monospace;
    }
    .check-list { list-style: none; padding-left: 0 !important; }
    .check-list li { position: relative; padding-left: 22px; }
    .check-list li::before { content: 'OK'; position: absolute; left: 0; color: var(--success); font: 700 10px 'JetBrains Mono', monospace; }
    .coverage-note {
      padding: 14px 16px;
      border-left: 3px solid var(--accent);
      background: var(--accent-dim);
      border-radius: 0 8px 8px 0;
      color: var(--text-2); font-size: 13px; line-height: 1.6;
      margin-bottom: 20px;
    }

    @media (max-width: 640px) {
      header { padding: 32px 20px 28px; }
      main { padding: 24px 16px 60px; }
      .doc-grid { grid-template-columns: 1fr; }
      .doc-card.wide { grid-column: auto; }
    }
  </style>
</head>
<body>

<!-- ═══════════════════════════ HEADER ═══════════════════════════ -->
<header>
  <div class="header-badge">⚙ UML Class Diagram</div>
  <h1>Smart Home Automation Simulator</h1>
  <p>Complete class architecture across 9 packages — models, services, automation engine, persistence, and Swing UI.</p>
  <div class="stats-row">
    <div class="stat-chip"><span class="num">6</span><span class="lbl">Device types</span></div>
    <div class="stat-chip"><span class="num">5</span><span class="lbl">Sensor types</span></div>
    <div class="stat-chip"><span class="num">4</span><span class="lbl">Interfaces</span></div>
    <div class="stat-chip"><span class="num">7</span><span class="lbl">Services</span></div>
    <div class="stat-chip"><span class="num">5</span><span class="lbl">Design patterns</span></div>
    <div class="stat-chip"><span class="num">9</span><span class="lbl">Packages</span></div>
  </div>
</header>

<!-- ═══════════════════════════ MAIN ═══════════════════════════ -->
<main>

  <!-- PACKAGE OVERVIEW -->
  <div class="section-title">Package Overview</div>
  <div class="pkg-grid">

    <div class="pkg-card" style="--pkg-color:#6ea8e8">
      <div class="pkg-icon">🔌</div>
      <h3>smarthome.interfaces</h3>
      <p>Core contracts defining device capabilities and event listeners.</p>
      <div class="class-list">
        <span class="cls-tag iface">Switchable</span>
        <span class="cls-tag iface">Controllable</span>
        <span class="cls-tag iface">Alertable</span>
        <span class="cls-tag iface">SensorListener</span>
      </div>
    </div>

    <div class="pkg-card" style="--pkg-color:#3dd6c0">
      <div class="pkg-icon">🏠</div>
      <h3>smarthome.model</h3>
      <p>All domain entities. <code>Home</code> is the aggregate root composing every collection.</p>
      <div class="class-list">
        <span class="cls-tag">Home</span>
        <span class="cls-tag">Room</span>
        <span class="cls-tag abstract">Device</span>
        <span class="cls-tag abstract">Sensor</span>
        <span class="cls-tag">Alert</span>
        <span class="cls-tag">Schedule</span>
        <span class="cls-tag">SystemLog</span>
        <span class="cls-tag">AutomationRule</span>
      </div>
    </div>

    <div class="pkg-card" style="--pkg-color:#e8ae3d">
      <div class="pkg-icon">💡</div>
      <h3>smarthome.model — Devices</h3>
      <p>Six concrete smart device types, all extending the abstract <code>Device</code>.</p>
      <div class="class-list">
        <span class="cls-tag">Light</span>
        <span class="cls-tag">Fan</span>
        <span class="cls-tag">AirConditioner</span>
        <span class="cls-tag">SmartTV</span>
        <span class="cls-tag">SmartLock</span>
        <span class="cls-tag">SecurityCamera</span>
      </div>
    </div>

    <div class="pkg-card" style="--pkg-color:#a78bfa">
      <div class="pkg-icon">📡</div>
      <h3>smarthome.model — Sensors</h3>
      <p>Five concrete sensor types, all extending the abstract <code>Sensor</code>.</p>
      <div class="class-list">
        <span class="cls-tag">TemperatureSensor</span>
        <span class="cls-tag">MotionSensor</span>
        <span class="cls-tag">DoorSensor</span>
        <span class="cls-tag">SmokeSensor</span>
        <span class="cls-tag">LightSensor</span>
      </div>
    </div>

    <div class="pkg-card" style="--pkg-color:#4cc97a">
      <div class="pkg-icon">⚡</div>
      <h3>smarthome.automation</h3>
      <p>Strategy + Observer patterns. Engine evaluates IF→THEN rules on every sensor event.</p>
      <div class="class-list">
        <span class="cls-tag abstract">Condition</span>
        <span class="cls-tag abstract">Action</span>
        <span class="cls-tag">AutomationEngine</span>
        <span class="cls-tag">TurnOnDeviceAction</span>
        <span class="cls-tag">TurnOffDeviceAction</span>
        <span class="cls-tag">RaiseAlertAction</span>
      </div>
    </div>

    <div class="pkg-card" style="--pkg-color:#3dd6c0">
      <div class="pkg-icon">🧩</div>
      <h3>smarthome.service</h3>
      <p>Business logic layer. Each service operates on the shared <code>Home</code> object.</p>
      <div class="class-list">
        <span class="cls-tag">HomeService</span>
        <span class="cls-tag">DeviceService</span>
        <span class="cls-tag">SensorService</span>
        <span class="cls-tag">AutomationService</span>
        <span class="cls-tag">ScheduleService</span>
        <span class="cls-tag">AlertService</span>
        <span class="cls-tag">SecurityService</span>
      </div>
    </div>

    <div class="pkg-card" style="--pkg-color:#6ea8e8">
      <div class="pkg-icon">💾</div>
      <h3>smarthome.persistence</h3>
      <p>Java object serialisation — saves/loads the entire <code>Home</code> graph to <code>data/home.dat</code>.</p>
      <div class="class-list">
        <span class="cls-tag">FileManager</span>
      </div>
    </div>

    <div class="pkg-card" style="--pkg-color:#e86a6a">
      <div class="pkg-icon">⚠</div>
      <h3>smarthome.exception</h3>
      <p>Custom runtime exceptions for domain-specific error cases.</p>
      <div class="class-list">
        <span class="cls-tag">DeviceNotFoundException</span>
        <span class="cls-tag">RoomNotFoundException</span>
        <span class="cls-tag">InvalidDeviceStateException</span>
        <span class="cls-tag">InvalidScheduleException</span>
      </div>
    </div>

    <div class="pkg-card" style="--pkg-color:#a78bfa">
      <div class="pkg-icon">🖥</div>
      <h3>smarthome.ui</h3>
      <p>Swing GUI — dark-themed panels, reusable components, centralised theme constants.</p>
      <div class="class-list">
        <span class="cls-tag">MainFrame</span>
        <span class="cls-tag">ConsoleUI</span>
        <span class="cls-tag">MenuController</span>
        <span class="cls-tag">InputValidator</span>
        <span class="cls-tag">Theme</span>
        <span class="cls-tag">DashboardPanel</span>
        <span class="cls-tag">DevicePanel</span>
        <span class="cls-tag">SensorPanel</span>
        <span class="cls-tag">RoomPanel</span>
        <span class="cls-tag">AutomationPanel</span>
        <span class="cls-tag">SchedulePanel</span>
        <span class="cls-tag">SecurityPanel</span>
        <span class="cls-tag">AlertPanel</span>
        <span class="cls-tag">LogPanel</span>
      </div>
    </div>

  </div>

  <!-- DIAGRAM -->
  <div class="section-title">Full Class Diagram</div>
  <div class="diagram-panel">
    <div class="diagram-toolbar">
      <div style="display:flex;align-items:center;gap:16px">
        <div class="toolbar-dots">
          <div class="dot r"></div>
          <div class="dot y"></div>
          <div class="dot g"></div>
        </div>
        <span class="toolbar-title">📐 UML — smarthome.*</span>
      </div>
      <div style="display:flex;gap:8px">
        <button class="zoom-btn" onclick="zoom(-0.15)">− Zoom Out</button>
        <button class="zoom-btn" onclick="zoom(0.15)">+ Zoom In</button>
        <button class="zoom-btn" onclick="resetZoom()">↺ Reset</button>
      </div>
    </div>
    <div class="diagram-body">
      <div id="mermaid-wrap">
        <div class="mermaid">
classDiagram
    direction TB

    %% ── INTERFACES ──
    class Switchable {
        &lt;&lt;interface&gt;&gt;
        +turnOn() void
        +turnOff() void
        +isOn() boolean
    }
    class Controllable {
        &lt;&lt;interface&gt;&gt;
        +getStatusSummary() String
        +getCurrentPowerConsumption() double
    }
    class Alertable {
        &lt;&lt;interface&gt;&gt;
        +raiseAlert(alert Alert) void
    }
    class SensorListener {
        &lt;&lt;interface&gt;&gt;
        +onSensorEvent(sensor Sensor) void
    }

    %% ── ABSTRACT DEVICE ──
    class Device {
        &lt;&lt;abstract&gt;&gt;
        -deviceId String
        -deviceName String
        -roomId String
        -isOn boolean
        -createdTime LocalDateTime
        +turnOn() void
        +turnOff() void
        +isOn() boolean
        +getDeviceId() String
        +getDeviceName() String
        +getRoomId() String
        +getDeviceType()* String
        +getCurrentPowerConsumption()* double
        +getStatusSummary()* String
    }
    Device ..|> Switchable
    Device ..|> Controllable

    class Light {
        -brightness int
        +getDeviceType() String
        +getCurrentPowerConsumption() double
        +getStatusSummary() String
        +getBrightness() int
        +setBrightness(v int) void
    }
    class Fan {
        -speed int
        +getDeviceType() String
        +getCurrentPowerConsumption() double
        +getStatusSummary() String
        +getSpeed() int
        +setSpeed(v int) void
    }
    class AirConditioner {
        -targetTemperature double
        -mode String
        +getDeviceType() String
        +getCurrentPowerConsumption() double
        +getStatusSummary() String
        +getTargetTemperature() double
        +setTargetTemperature(t double) void
        +getMode() String
        +setMode(m String) void
    }
    class SmartTV {
        -channel int
        -volume int
        +getDeviceType() String
        +getCurrentPowerConsumption() double
        +getStatusSummary() String
    }
    class SmartLock {
        -locked boolean
        +getDeviceType() String
        +getCurrentPowerConsumption() double
        +getStatusSummary() String
        +isLocked() boolean
        +lock() void
        +unlock() void
    }
    class SecurityCamera {
        -monitoring boolean
        +getDeviceType() String
        +getCurrentPowerConsumption() double
        +getStatusSummary() String
        +isMonitoring() boolean
        +turnOn() void
        +turnOff() void
    }
    Device <|-- Light
    Device <|-- Fan
    Device <|-- AirConditioner
    Device <|-- SmartTV
    Device <|-- SmartLock
    Device <|-- SecurityCamera

    %% ── ABSTRACT SENSOR ──
    class Sensor {
        &lt;&lt;abstract&gt;&gt;
        -sensorId String
        -sensorName String
        -roomId String
        -active boolean
        +getSensorId() String
        +getSensorName() String
        +isActive() boolean
        +setActive(v boolean) void
        +getSensorType()* String
        +getFormattedValue()* String
    }
    class TemperatureSensor {
        -temperature double
        +getSensorType() String
        +getFormattedValue() String
        +getTemperature() double
        +setTemperature(t double) void
    }
    class MotionSensor {
        -motionDetected boolean
        +getSensorType() String
        +getFormattedValue() String
        +isMotionDetected() boolean
        +setMotionDetected(v boolean) void
    }
    class DoorSensor {
        -open boolean
        +getSensorType() String
        +getFormattedValue() String
        +isOpen() boolean
        +setOpen(v boolean) void
    }
    class SmokeSensor {
        -smokeDetected boolean
        +getSensorType() String
        +getFormattedValue() String
        +isSmokeDetected() boolean
    }
    class LightSensor {
        -luxLevel double
        +getSensorType() String
        +getFormattedValue() String
        +getLuxLevel() double
        +setLuxLevel(l double) void
    }
    Sensor <|-- TemperatureSensor
    Sensor <|-- MotionSensor
    Sensor <|-- DoorSensor
    Sensor <|-- SmokeSensor
    Sensor <|-- LightSensor

    %% ── CORE DOMAIN ──
    class Severity {
        &lt;&lt;enumeration&gt;&gt;
        INFO
        WARNING
        CRITICAL
    }
    class ScheduleAction {
        &lt;&lt;enumeration&gt;&gt;
        TURN_ON
        TURN_OFF
    }
    class EventType {
        &lt;&lt;enumeration&gt;&gt;
        DEVICE
        SENSOR
        AUTOMATION
        SECURITY
        SCHEDULE
        SYSTEM
    }
    class Home {
        -rooms Map
        -devices Map
        -sensors Map
        -automationRules Map
        -schedules Map
        -alerts Map
        -logs List
        -securityModeOn boolean
        +getRooms() Map
        +getDevices() Map
        +getSensors() Map
        +addLog(type EventType, desc String) void
        +isSecurityModeOn() boolean
        +setSecurityModeOn(v boolean) void
    }
    class Room {
        -roomId String
        -roomName String
        -deviceIds List
        -sensorIds List
        +getRoomId() String
        +getRoomName() String
        +addDeviceId(id String) void
        +removeDeviceId(id String) void
        +addSensorId(id String) void
        +removeSensorId(id String) void
    }
    class Alert {
        -alertId String
        -type String
        -message String
        -severity Severity
        -timestamp LocalDateTime
        -read boolean
        +getAlertId() String
        +getSeverity() Severity
        +isRead() boolean
        +markAsRead() void
    }
    class Schedule {
        -scheduleId String
        -scheduleName String
        -deviceId String
        -action ScheduleAction
        -scheduledTime LocalTime
        -enabled boolean
        -repeatDaily boolean
        +getScheduleId() String
        +getAction() ScheduleAction
        +isEnabled() boolean
        +isRepeatDaily() boolean
    }
    class SystemLog {
        -timestamp LocalDateTime
        -eventType EventType
        -description String
        +getFormattedTimestamp() String
        +getEventType() EventType
        +getDescription() String
    }
    class AutomationRule {
        -ruleId String
        -ruleName String
        -condition Condition
        -action Action
        -enabled boolean
        -creationTime LocalDateTime
        +getRuleId() String
        +isEnabled() boolean
        +getRuleDescription() String
    }

    Home "1" o-- "many" Room
    Home "1" *-- "many" Device
    Home "1" *-- "many" Sensor
    Home "1" *-- "many" Alert
    Home "1" *-- "many" Schedule
    Home "1" *-- "many" SystemLog
    Home "1" *-- "many" AutomationRule
    Alert --> Severity
    Schedule --> ScheduleAction
    SystemLog --> EventType

    %% ── AUTOMATION STRATEGY ──
    class Condition {
        &lt;&lt;abstract&gt;&gt;
        +evaluate(home Home)* boolean
        +describe()* String
    }
    class Action {
        &lt;&lt;abstract&gt;&gt;
        +execute(home Home, engine AutomationEngine)* void
        +describe()* String
    }
    class TemperatureAboveCondition {
        -sensorId String
        -threshold double
        +evaluate(home Home) boolean
        +describe() String
    }
    class MotionDetectedCondition {
        -sensorId String
        +evaluate(home Home) boolean
        +describe() String
    }
    class DoorOpenedCondition {
        -sensorId String
        +evaluate(home Home) boolean
        +describe() String
    }
    class SmokeDetectedCondition {
        -sensorId String
        +evaluate(home Home) boolean
        +describe() String
    }
    class LightLevelBelowCondition {
        -sensorId String
        -threshold double
        +evaluate(home Home) boolean
        +describe() String
    }
    class TurnOnDeviceAction {
        -deviceId String
        -deviceName String
        +execute(home Home, engine AutomationEngine) void
        +describe() String
    }
    class TurnOffDeviceAction {
        -deviceId String
        -deviceName String
        +execute(home Home, engine AutomationEngine) void
        +describe() String
    }
    class RaiseAlertAction {
        -alertType String
        -message String
        -severity Severity
        +execute(home Home, engine AutomationEngine) void
        +describe() String
    }

    AutomationRule o-- Condition
    AutomationRule o-- Action
    Condition <|-- TemperatureAboveCondition
    Condition <|-- MotionDetectedCondition
    Condition <|-- DoorOpenedCondition
    Condition <|-- SmokeDetectedCondition
    Condition <|-- LightLevelBelowCondition
    Action <|-- TurnOnDeviceAction
    Action <|-- TurnOffDeviceAction
    Action <|-- RaiseAlertAction

    %% ── AUTOMATION ENGINE ──
    class AutomationEngine {
        -home Home
        -alertCallbacks List
        -logCallbacks List
        +onSensorEvent(sensor Sensor) void
        +evaluateAllRules() void
        +raiseAlert(alert Alert) void
        +logAutomationEvent(desc String) void
        +addAlertCallback(cb Consumer) void
        +addLogCallback(cb Consumer) void
    }
    AutomationEngine ..|> SensorListener
    AutomationEngine ..|> Alertable
    AutomationEngine --> Home

    %% ── SERVICES ──
    class HomeService {
        -home Home
        -fileManager FileManager
        +getHome() Home
        +seedInitialData() void
        +save() void
        +load() void
    }
    class DeviceService {
        -home Home
        +addDevice(device Device) void
        +removeDevice(id String) void
        +toggleDevice(id String) void
        +getDevicesInRoom(roomId String) List
        +updateDevice(device Device) void
    }
    class SensorService {
        -home Home
        -listeners List
        +addSensor(sensor Sensor) void
        +removeSensor(id String) void
        +updateSensorValue(sensor Sensor) void
        +addListener(l SensorListener) void
        +notifyListeners(sensor Sensor) void
        +startSimulation() void
        +stopSimulation() void
    }
    class AutomationService {
        -home Home
        -engine AutomationEngine
        +addRule(rule AutomationRule) void
        +removeRule(id String) void
        +toggleRule(id String) void
        +triggerManually() void
    }
    class ScheduleService {
        -home Home
        +addSchedule(schedule Schedule) void
        +removeSchedule(id String) void
        +toggleSchedule(id String) void
        +startScheduler() void
        +stopScheduler() void
    }
    class AlertService {
        -home Home
        +addAlert(alert Alert) void
        +markAllRead() void
        +getUnreadCount() int
        +clearAll() void
    }
    class SecurityService {
        -home Home
        +isArmed() boolean
        +arm() void
        +disarm() void
    }
    class FileManager {
        -dataFilePath String
        +save(home Home) void
        +load() Home
    }

    HomeService --> Home
    HomeService --> FileManager
    DeviceService --> Home
    SensorService --> Home
    SensorService --> SensorListener
    AutomationService --> Home
    AutomationService --> AutomationEngine
    ScheduleService --> Home
    AlertService --> Home
    SecurityService --> Home

    %% ── EXCEPTIONS ──
    class DeviceNotFoundException {
        +DeviceNotFoundException(id String)
    }
    class RoomNotFoundException {
        +RoomNotFoundException(id String)
    }
    class InvalidDeviceStateException {
        +InvalidDeviceStateException(msg String)
    }
    class InvalidScheduleException {
        +InvalidScheduleException(msg String)
    }
    DeviceNotFoundException --|> RuntimeException
    RoomNotFoundException --|> RuntimeException
    InvalidDeviceStateException --|> RuntimeException
    InvalidScheduleException --|> RuntimeException

    %% ── ENTRY POINT ──
    class Main {
        +main(args String[]) void
    }
    class MainFrame {
        -homeService HomeService
        -deviceService DeviceService
        -sensorService SensorService
        -automationService AutomationService
        -scheduleService ScheduleService
        -alertService AlertService
        -securityService SecurityService
        +show() void
    }
    class ConsoleUI {
        -menuController MenuController
        -inputValidator InputValidator
        +start() void
        +showMainMenu() void
        +readCommand() int
        +displayMessage(message String) void
    }
    class MenuController {
        -homeService HomeService
        -deviceService DeviceService
        -sensorService SensorService
        -automationService AutomationService
        -scheduleService ScheduleService
        -alertService AlertService
        -securityService SecurityService
        +handleCommand(choice int) void
        +showDashboard() void
    }
    class InputValidator {
        +requireNonBlank(value String, field String) String
        +parseInt(value String, field String) int
        +parseDouble(value String, field String) double
        +validateTime(value String) LocalTime
    }
    class Theme {
        &lt;&lt;utility&gt;&gt;
        +BACKGROUND Color
        +SURFACE Color
        +ACCENT Color
        +TEXT_PRIMARY Color
        +TITLE_FONT Font
    }
    class DashboardPanel {
        +refreshSummary() void
        +showPowerUsage() void
    }
    class DevicePanel {
        +refreshDevices() void
        +showDeviceEditor() void
    }
    class SensorPanel {
        +refreshSensors() void
        +showSensorEditor() void
    }
    class RoomPanel {
        +refreshRooms() void
        +showRoomEditor() void
    }
    class AutomationPanel {
        +refreshRules() void
        +showRuleEditor() void
    }
    class SchedulePanel {
        +refreshSchedules() void
        +showScheduleEditor() void
    }
    class SecurityPanel {
        +refreshSecurityState() void
        +toggleArmedState() void
    }
    class AlertPanel {
        +refreshAlerts() void
        +markAllRead() void
    }
    class LogPanel {
        +refreshLogs() void
        +filterByEventType() void
    }
    Main --> MainFrame
    Main --> ConsoleUI
    MainFrame --> HomeService
    MainFrame --> DeviceService
    MainFrame --> SensorService
    MainFrame --> AutomationService
    MainFrame --> ScheduleService
    MainFrame --> AlertService
    MainFrame --> SecurityService
    MainFrame --> Theme
    MainFrame *-- DashboardPanel
    MainFrame *-- DevicePanel
    MainFrame *-- SensorPanel
    MainFrame *-- RoomPanel
    MainFrame *-- AutomationPanel
    MainFrame *-- SchedulePanel
    MainFrame *-- SecurityPanel
    MainFrame *-- AlertPanel
    MainFrame *-- LogPanel
    DashboardPanel --> HomeService
    DevicePanel --> DeviceService
    SensorPanel --> SensorService
    RoomPanel --> HomeService
    AutomationPanel --> AutomationService
    SchedulePanel --> ScheduleService
    SecurityPanel --> SecurityService
    AlertPanel --> AlertService
    LogPanel --> HomeService
    ConsoleUI --> MenuController
    ConsoleUI --> InputValidator
    MenuController --> HomeService
    MenuController --> DeviceService
    MenuController --> SensorService
    MenuController --> AutomationService
    MenuController --> ScheduleService
    MenuController --> AlertService
    MenuController --> SecurityService
        </div>
      </div>
    </div>
  </div>

  <!-- LEGEND -->
  <div class="legend">
    <div class="legend-item">
      <div class="legend-line solid"></div>
      <span><strong>--|&gt;</strong> Inheritance (extends)</span>
    </div>
    <div class="legend-item">
      <div class="legend-line dashed"></div>
      <span><strong>..|&gt;</strong> Realisation (implements)</span>
    </div>
    <div class="legend-item">
      <div class="legend-line compose"></div>
      <span><strong>*--</strong> Composition (strong ownership)</span>
    </div>
    <div class="legend-item">
      <div class="legend-line dotted"></div>
      <span><strong>o--</strong> Aggregation (shared reference)</span>
    </div>
    <div class="legend-item">
      <div class="legend-line" style="background:var(--text-3)"></div>
      <span><strong>--&gt;</strong> Dependency / uses</span>
    </div>
    <div class="legend-item">
      <div style="width:36px;height:2px;border-top:2px dashed var(--text-3)"></div>
      <span><strong>--</strong> Association</span>
    </div>
  </div>

  <!-- DESIGN PATTERNS TABLE -->
  <div class="section-title">Design Patterns</div>
  <div class="table-wrap">
    <table>
      <thead>
        <tr>
          <th>Pattern</th>
          <th>Where Applied</th>
          <th>Classes Involved</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td><span class="pattern-badge p-inherit">Inheritance</span></td>
          <td>All device and sensor variants share a common base class with template methods.</td>
          <td><code>Device</code> → <code>Light</code>, <code>Fan</code>, <code>AirConditioner</code>, <code>SmartTV</code>, <code>SmartLock</code>, <code>SecurityCamera</code><br><code>Sensor</code> → 5 concrete sensor types</td>
        </tr>
        <tr>
          <td><span class="pattern-badge p-strategy">Strategy</span></td>
          <td>Conditions and Actions are pluggable strategies held by <code>AutomationRule</code>. New IF/THEN logic can be added without changing the rule or engine.</td>
          <td><code>Condition</code> (abstract) + 5 concrete conditions<br><code>Action</code> (abstract) + 3 concrete actions<br><code>AutomationRule</code> (context)</td>
        </tr>
        <tr>
          <td><span class="pattern-badge p-observer">Observer</span></td>
          <td><code>SensorService</code> is the publisher; registered <code>SensorListener</code> objects are notified on every sensor state change. <code>AutomationEngine</code> subscribes as an observer to trigger rule evaluation.</td>
          <td><code>SensorListener</code> (interface)<br><code>SensorService</code> (publisher)<br><code>AutomationEngine</code> (subscriber)</td>
        </tr>
        <tr>
          <td><span class="pattern-badge p-composite">Composition</span></td>
          <td><code>Home</code> is the aggregate root. All collections (rooms, devices, sensors, rules, schedules, alerts, logs) are owned by <code>Home</code> and serialised together as one object graph.</td>
          <td><code>Home</code> *── <code>Room</code>, <code>Device</code>, <code>Sensor</code>, <code>Alert</code>, <code>Schedule</code>, <code>SystemLog</code>, <code>AutomationRule</code></td>
        </tr>
        <tr>
          <td><span class="pattern-badge p-template">Template Method</span></td>
          <td>Abstract base classes define the skeleton algorithm (e.g. <code>toString()</code> calls abstract hooks), while subclasses fill in the specific steps like <code>getDeviceType()</code> and <code>getStatusSummary()</code>.</td>
          <td><code>Device</code>, <code>Sensor</code> (template)<br>All concrete device / sensor subclasses (concrete steps)</td>
        </tr>
        <tr>
          <td><span class="pattern-badge p-singleton">Utility / Singleton-like</span></td>
          <td><code>Theme</code> is a final class with only static constants — a non-instantiable utility providing all color and font tokens to the UI.</td>
          <td><code>Theme</code> (smarthome.ui.theme)</td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- MINI PROJECT REPORT COVERAGE -->
  <div class="section-title">Mini Project Report Coverage</div>
  <div class="coverage-note">
    This section turns the UML into a complete Java mini-project blueprint. It covers the PDF checklist: problem statement, motivation, scope and limitations, module split-up, implementation, interactive input/output, file storage, OOP features, design alternatives, testing, documentation, inference, and future extensions.
  </div>

  <div class="doc-grid">
    <article class="doc-card">
      <span class="eyebrow">Problem statement</span>
      <h3>Smart Home Automation Simulator</h3>
      <p>Build a Java application that lets a resident configure rooms, smart devices, sensors, schedules, security controls, and automation rules. The system must monitor sensor changes, apply IF/THEN rules, notify the user of alerts, and preserve the home state between runs.</p>
    </article>

    <article class="doc-card">
      <span class="eyebrow">Motivation</span>
      <h3>One place for routine and safety</h3>
      <p>A home can contain many independently controlled appliances and safety sensors. Bringing them into one simulator demonstrates how automation can save energy, reduce repetitive actions, and respond consistently to motion, temperature, door, smoke, and light events.</p>
    </article>

    <article class="doc-card">
      <span class="eyebrow">Inputs and outputs</span>
      <h3>Interactive application contract</h3>
      <ul>
        <li><strong>Inputs:</strong> room and device details, device settings, sensor readings, rules, schedules, security actions, and menu commands.</li>
        <li><strong>Outputs:</strong> dashboard summaries, device/sensor status, schedules, alerts, system logs, validation prompts, and saved-data confirmation.</li>
        <li><strong>Interface:</strong> <code>ConsoleUI</code> provides the required menu-driven text interface; <code>MainFrame</code> is the Swing dashboard view.</li>
      </ul>
    </article>

    <article class="doc-card">
      <span class="eyebrow">Scope and limitations</span>
      <h3>Bounded simulation</h3>
      <ul>
        <li>Simulates one home and its local devices; it does not control physical IoT hardware.</li>
        <li>Sensor values are entered or generated by the application rather than read from real sensors.</li>
        <li>Data is stored locally in <code>data/home.dat</code>; there is no cloud sync, multi-user login, or network security layer.</li>
        <li>Schedules use the local machine clock and are evaluated while the application is running.</li>
      </ul>
    </article>

    <article class="doc-card wide">
      <span class="eyebrow">End-to-end control flow</span>
      <h3>How a sensor event becomes an action</h3>
      <ol class="flow-list">
        <li>Read a validated command or sensor value from the console or GUI.</li>
        <li><code>SensorService</code> updates the sensor and records the change.</li>
        <li>Registered <code>SensorListener</code> objects receive the event.</li>
        <li><code>AutomationEngine</code> evaluates enabled rule conditions.</li>
        <li>The selected action changes a device or creates an alert.</li>
        <li><code>AlertService</code>, dashboard, console, and logs display the result.</li>
        <li><code>HomeService</code> saves the complete object graph through <code>FileManager</code>.</li>
      </ol>
    </article>
  </div>

  <div class="section-title">Module Split-Up and Implementation</div>
  <div class="table-wrap">
    <table>
      <thead>
        <tr>
          <th>Module / package</th>
          <th>Responsibility</th>
          <th>Key classes</th>
        </tr>
      </thead>
      <tbody>
        <tr><td><code>smarthome.model</code></td><td>Owns the home state and all domain data.</td><td><code>Home</code>, <code>Room</code>, <code>Device</code>, <code>Sensor</code>, <code>Alert</code>, <code>Schedule</code></td></tr>
        <tr><td><code>smarthome.interfaces</code></td><td>Defines stable capability and event contracts.</td><td><code>Switchable</code>, <code>Controllable</code>, <code>Alertable</code>, <code>SensorListener</code></td></tr>
        <tr><td><code>smarthome.automation</code></td><td>Evaluates pluggable conditions and executes actions.</td><td><code>AutomationEngine</code>, <code>Condition</code>, <code>Action</code>, <code>AutomationRule</code></td></tr>
        <tr><td><code>smarthome.service</code></td><td>Contains the application use cases and integration logic.</td><td><code>DeviceService</code>, <code>SensorService</code>, <code>ScheduleService</code>, <code>SecurityService</code></td></tr>
        <tr><td><code>smarthome.persistence</code></td><td>Saves and restores the serialised <code>Home</code> aggregate.</td><td><code>FileManager</code>, <code>HomeService</code></td></tr>
        <tr><td><code>smarthome.ui</code></td><td>Accepts user input and presents status in text and Swing views.</td><td><code>ConsoleUI</code>, <code>MenuController</code>, <code>InputValidator</code>, <code>MainFrame</code>, <code>Theme</code></td></tr>
        <tr><td><code>smarthome.exception</code></td><td>Reports invalid states and missing resources with clear messages.</td><td><code>DeviceNotFoundException</code>, <code>RoomNotFoundException</code>, <code>InvalidScheduleException</code></td></tr>
      </tbody>
    </table>
  </div>

  <div class="doc-grid" style="margin-top:24px">
    <article class="doc-card">
      <span class="eyebrow">Implementation specifics</span>
      <h3>Java design decisions</h3>
      <ul>
        <li>Keep one public responsibility per class and one feature area per package.</li>
        <li>Place each class, interface, and enum in a distinct, clearly named Java source file.</li>
        <li>Use <code>Map&lt;String, ...&gt;</code> for ID lookup and <code>List</code> for ordered logs and listener callbacks.</li>
        <li>Use <code>LocalDateTime</code> and <code>LocalTime</code> for audit records and schedule evaluation.</li>
        <li>Persist the complete <code>Home</code> aggregate with Java object serialisation so related data is restored together.</li>
        <li>Validate console input before a service call and return domain-specific exceptions when state is invalid.</li>
      </ul>
    </article>

    <article class="doc-card">
      <span class="eyebrow">Design alternative</span>
      <h3>Rule engine choices</h3>
      <p><strong>Alternative:</strong> place every automation case in a single <code>if/else</code> method inside <code>SensorService</code>. This is quick for a few rules, but every new condition or action changes existing code.</p>
      <p style="margin-top:9px"><strong>Selected design:</strong> compose <code>AutomationRule</code> from <code>Condition</code> and <code>Action</code> strategies. The engine remains closed to modification while new rules are added as focused classes, and the Observer link keeps sensors decoupled from automation.</p>
    </article>
  </div>

  <div class="section-title">Object-Oriented Features Used</div>
  <div class="table-wrap">
    <table>
      <thead>
        <tr><th>Feature</th><th>Application in this project</th><th>Benefit</th></tr>
      </thead>
      <tbody>
        <tr><td>Encapsulation</td><td>Private fields with validated service methods and getters/setters in devices, sensors, schedules, and alerts.</td><td>Protects state such as a lock's status and a schedule's enabled flag.</td></tr>
        <tr><td>Abstraction</td><td><code>Device</code>, <code>Sensor</code>, <code>Condition</code>, and <code>Action</code> expose common operations while hiding details.</td><td>Lets services work with general types.</td></tr>
        <tr><td>Inheritance</td><td>Concrete devices and sensors extend their respective abstract base classes.</td><td>Reuses common identity, room, power, and status behaviour.</td></tr>
        <tr><td>Polymorphism</td><td>Services and the automation engine operate on <code>Device</code>, <code>Sensor</code>, <code>Condition</code>, and <code>Action</code> references.</td><td>Supports new variants without rewriting callers.</td></tr>
        <tr><td>Interfaces</td><td><code>Switchable</code>, <code>Controllable</code>, <code>Alertable</code>, and <code>SensorListener</code> define capabilities.</td><td>Reduces coupling and supports multiple implementations.</td></tr>
        <tr><td>Composition</td><td><code>Home</code> owns its rooms, devices, sensors, rules, schedules, alerts, and logs.</td><td>Keeps the persistent model coherent.</td></tr>
        <tr><td>Exception handling</td><td>Custom exceptions communicate missing IDs, invalid device states, and invalid schedules.</td><td>Produces actionable prompts instead of silent failures.</td></tr>
      </tbody>
    </table>
  </div>

  <div class="section-title">Test Cases, Demonstration, and Documentation</div>
  <div class="doc-grid">
    <article class="doc-card wide">
      <span class="eyebrow">Test plan</span>
      <h3>Demonstrate every use case</h3>
      <div class="table-wrap">
        <table>
          <thead><tr><th>Test case</th><th>Action</th><th>Expected result</th></tr></thead>
          <tbody>
            <tr><td>1. Input validation</td><td>Enter an empty room name, non-numeric device setting, or invalid time.</td><td>A prompt explains the problem and retains a consistent state.</td></tr>
            <tr><td>2. Room and device setup</td><td>Create a room and add each device type.</td><td>The dashboard lists the correct room assignment and device status.</td></tr>
            <tr><td>3. Device control</td><td>Toggle a light/fan/AC and update brightness, speed, or temperature.</td><td>The device summary and power consumption change appropriately.</td></tr>
            <tr><td>4. Sensor event</td><td>Update motion, door, smoke, temperature, or light readings.</td><td>The sensor state, log, and subscribed listeners are updated.</td></tr>
            <tr><td>5. Automation rule</td><td>Create and trigger a matching IF/THEN rule.</td><td>The configured action turns a device on/off or raises an alert.</td></tr>
            <tr><td>6. Schedule</td><td>Create an enabled scheduled action at the current test time.</td><td>The target device changes state and a schedule log is written.</td></tr>
            <tr><td>7. Security</td><td>Arm the home, open a door, or detect smoke/motion.</td><td>A warning/critical alert is shown and marked unread.</td></tr>
            <tr><td>8. Alert management</td><td>Mark alerts read and clear them.</td><td>Unread count and alert list are refreshed.</td></tr>
            <tr><td>9. Persistence</td><td>Save, restart, and load the home.</td><td>Rooms, rules, schedules, and logs are restored from the data file.</td></tr>
            <tr><td>10. Exception paths</td><td>Request a missing device/room or invalid schedule.</td><td>The matching custom exception is handled with a useful message.</td></tr>
          </tbody>
        </table>
      </div>
    </article>

    <article class="doc-card">
      <span class="eyebrow">Output screenshots</span>
      <h3>Capture these during the demo</h3>
      <ul class="check-list">
        <li>Console main menu and initial dashboard</li>
        <li>Adding a room and device with validation prompts</li>
        <li>Sensor event triggering an automation rule</li>
        <li>Schedule execution and system log entry</li>
        <li>Security alert and read/unread management</li>
        <li>Save/load confirmation after restart</li>
      </ul>
    </article>

    <article class="doc-card">
      <span class="eyebrow">Best practices</span>
      <h3>Quality checklist</h3>
      <ul class="check-list">
        <li>Use the class diagram as the design reference.</li>
        <li>Use clear Java naming conventions for packages, classes, fields, and methods.</li>
        <li>Comment intent and non-obvious logic; do not restate code.</li>
        <li>Show clear prompts for all text input and output.</li>
        <li>Develop incrementally: model, services, persistence, automation, then UI.</li>
        <li>Keep modules independent and run the complete test plan before presentation.</li>
      </ul>
    </article>

    <article class="doc-card wide">
      <span class="eyebrow">Evaluation delivery</span>
      <h3>Suggested presentation order</h3>
      <p>Obtain approval for the problem statement before implementation. During evaluation, present the motivation and scope, walk through this class diagram and module split-up, run the ten test cases through the console or GUI, show the saved data and output screenshots, then close with the OOP features, design alternative, limitations, and future extensions.</p>
    </article>

    <article class="doc-card wide">
      <span class="eyebrow">Inference and future extension</span>
      <h3>What the project demonstrates next</h3>
      <p>The simulator shows that a layered Java design can integrate independent devices, live sensor events, rules, schedules, security, and persistence without making the UI depend on device-specific code. Future work can add a database repository, user roles, MQTT or REST-based hardware adapters, mobile/web clients, real-time notifications, rule editing, analytics for power consumption, and test doubles for external devices.</p>
    </article>
  </div>

</main>

<footer>
  Generated for <strong>Smart Home Automation Simulator</strong> &mdash; Java 17+ / OpenJDK 21 &mdash; Standard Library Only
</footer>

<script>
  // ── Mermaid init ──
  mermaid.initialize({
    startOnLoad: true,
    theme: 'dark',
    themeVariables: {
      background:        '#0d1117',
      primaryColor:      '#1c2230',
      primaryBorderColor:'#3dd6c0',
      primaryTextColor:  '#e8ecf1',
      lineColor:         '#3dd6c0',
      secondaryColor:    '#21293a',
      tertiaryColor:     '#161b22',
      edgeLabelBackground:'#161b22',
      classText:         '#e8ecf1',
      titleColor:        '#3dd6c0',
      nodeBorder:        '#2d3748',
      clusterBkg:        '#161b22',
      fontFamily:        'Inter, sans-serif',
      fontSize:          '13px'
    },
    classDiagram: { useMaxWidth: false }
  });

  // ── Zoom controls ──
  let scale = 1;
  const wrap = document.getElementById('mermaid-wrap');

  function zoom(delta) {
    scale = Math.min(Math.max(scale + delta, 0.3), 3);
    wrap.style.transform = `scale(${scale})`;
  }
  function resetZoom() {
    scale = 1;
    wrap.style.transform = 'scale(1)';
  }

  // ── Fade-in on load ──
  document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.pkg-card').forEach((card, i) => {
      card.style.opacity = '0';
      card.style.transform = 'translateY(18px)';
      setTimeout(() => {
        card.style.transition = 'opacity .4s ease, transform .4s ease';
        card.style.opacity = '1';
        card.style.transform = 'translateY(0)';
      }, 80 + i * 60);
    });
  });
</script>
</body>
</html>

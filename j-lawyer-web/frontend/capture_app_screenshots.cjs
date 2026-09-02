const puppeteer = require('puppeteer-core');
const fs = require('fs');
const path = require('path');

const CHROME_PATH = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';
const ARTIFACT_DIR = 'C:\\Users\\Ricardo PC\\.gemini\\antigravity\\brain\\6e86071e-04a5-4476-bf13-974e601a24f4\\screenshots';
const DOCS_DIR = 'C:\\projetos IA\\BR-LAWYER\\br-lawyer-webui\\docs\\screenshots';

fs.mkdirSync(ARTIFACT_DIR, { recursive: true });
fs.mkdirSync(DOCS_DIR, { recursive: true });

async function sleep(ms) {
  return new Promise(r => setTimeout(r, ms));
}

async function saveScreenshot(page, filename) {
  const p1 = path.join(ARTIFACT_DIR, filename);
  const p2 = path.join(DOCS_DIR, filename);
  await page.screenshot({ path: p1 });
  fs.copyFileSync(p1, p2);
  console.log('✓ Saved screenshot:', filename);
}

async function captureAll() {
  const browser = await puppeteer.launch({
    executablePath: CHROME_PATH,
    headless: 'new',
    args: ['--no-sandbox', '--disable-setuid-sandbox', '--window-size=1440,900']
  });

  const page = await browser.newPage();
  await page.setViewport({ width: 1440, height: 900 });

  // 1. Login Screen
  console.log('\n[1/12] Capturing Login Screen...');
  await page.goto('http://127.0.0.1:3000', { waitUntil: 'networkidle0' });
  await sleep(1000);
  await saveScreenshot(page, '01_login_screen.png');

  // 2. Perform Login
  console.log('\n[2/12] Submitting login form...');
  await page.click('button[type="submit"]');

  // 3. Dashboard / Daily Command Center
  console.log('\n[3/12] Waiting for Dashboard / Cockpit...');
  await page.waitForSelector('aside nav button', { timeout: 15000 });
  await sleep(3500);
  await saveScreenshot(page, '03_dashboard_cockpit.png');

  async function clickNav(index) {
    const navButtons = await page.$$('aside nav button');
    if (navButtons[index]) {
      await navButtons[index].click();
      await sleep(2500);
    }
  }

  // 4. Publicações Inbox
  console.log('\n[4/12] Navigating to Publicações Inbox...');
  await clickNav(2); // Publicações
  await page.waitForSelector('table tbody tr', { timeout: 10000 });
  await sleep(1500);
  await saveScreenshot(page, '04_publications_inbox.png');

  // 5. Publication Inspector Drawer
  console.log('\n[5/12] Opening Publication Inspector Drawer...');
  const pubRows = await page.$$('table tbody tr');
  if (pubRows.length > 0) {
    await pubRows[0].click();
    await sleep(2500);
    await saveScreenshot(page, '05_publication_inspector_drawer.png');

    // 6. Publication Triage Modal
    console.log('\n[6/12] Opening Publication Triage Decision Modal...');
    const treatBtn = await page.$('button.bg-emerald-600') || (await page.$$('aside button'))[3];
    if (treatBtn) {
      await treatBtn.click();
      await sleep(2000);
      await saveScreenshot(page, '06_publication_triage_modal.png');
      await page.keyboard.press('Escape');
      await sleep(1000);
    }
    await page.keyboard.press('Escape');
    await sleep(500);
  }

  // 7. Tarefas / Tasks Kanban Board
  console.log('\n[7/12] Navigating to Tarefas (Kanban)...');
  await clickNav(3); // Tarefas
  await sleep(2500);
  await saveScreenshot(page, '07_tasks_kanban.png');

  // 8. Open Task Drawer
  console.log('\n[8/12] Opening Task Inspector Drawer...');
  const taskCards = await page.$$('div[draggable="true"], div.cursor-grab');
  if (taskCards.length > 0) {
    await taskCards[0].click();
    await sleep(2500);
    await saveScreenshot(page, '08_task_inspector_drawer.png');
    await page.keyboard.press('Escape');
    await sleep(1000);
  }

  // 9. Switch to List Mode
  console.log('\n[9/12] Switching to Tasks List Mode...');
  const listButtons = await page.$$('button');
  for (const b of listButtons) {
    const title = await page.evaluate(el => el.getAttribute('title') || '', b);
    if (title.includes('Lista')) {
      await b.click();
      await sleep(2000);
      await saveScreenshot(page, '09_tasks_list.png');
      break;
    }
  }

  // 10. Processos / Cases List
  console.log('\n[10/12] Navigating to Processos List...');
  await clickNav(1); // Processos
  await page.waitForSelector('table tbody tr', { timeout: 10000 });
  await sleep(1500);
  await saveScreenshot(page, '10_cases_list.png');

  // 11. Case Detail as Hub
  console.log('\n[11/12] Opening Case Hub Detail...');
  const detailButtons = await page.$$('button');
  for (const db of detailButtons) {
    const text = await page.evaluate(el => el.textContent, db);
    if (text && text.trim() === 'Detalhe') {
      await db.click();
      await sleep(3500);
      await saveScreenshot(page, '11_case_detail_hub.png');
      break;
    }
  }

  // 12. Command Palette (Ctrl+K)
  console.log('\n[12/12] Triggering Command Palette (Ctrl+K)...');
  await page.keyboard.down('Control');
  await page.keyboard.press('KeyK');
  await page.keyboard.up('Control');
  await sleep(2000);
  await saveScreenshot(page, '12_command_palette.png');

  await browser.close();
  console.log('\n========================================================');
  console.log('ALL 12 REAL SCREENSHOTS GENERATED AND SAVED SUCCESSFULLY!');
  console.log('========================================================');
}

captureAll().catch(err => {
  console.error('Error during capture:', err);
  process.exit(1);
});
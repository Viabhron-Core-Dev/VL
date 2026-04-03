/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

export default function App() {
  return (
    <div className="min-h-screen bg-[#0D0D0D] text-[#FFFFFF] font-sans p-8 flex flex-col items-center justify-center text-center">
      <div className="max-w-2xl space-y-6">
        <div className="flex items-center justify-center space-x-4">
          <div className="w-16 h-16 bg-gradient-to-b from-[#3700B3] to-[#1A0066] rounded-2xl flex items-center justify-center text-2xl font-bold border border-white/10 shadow-2xl">
            VF
          </div>
          <h1 className="text-4xl font-bold tracking-tight">VibeForge Vian Launcher</h1>
        </div>
        
        <p className="text-lg text-[#AAAAAA]">
          Privacy-focused Android home screen replacement based on KISS Launcher.
        </p>
        
        <div className="bg-[#1A1A1A] border border-[#252525] rounded-xl p-6 text-left space-y-4 shadow-xl">
          <div className="flex items-center justify-between border-b border-[#252525] pb-2 mb-4">
            <h2 className="text-xl font-semibold">Project Status</h2>
            <span className="px-2 py-1 bg-[#4CAF50]/20 text-[#4CAF50] text-xs font-mono rounded uppercase tracking-wider">Build Ready</span>
          </div>
          
          <div className="grid grid-cols-2 gap-4 text-sm font-mono">
            <div className="space-y-1">
              <p className="text-[#AAAAAA]">Package</p>
              <p>com.vibeforge.vian</p>
            </div>
            <div className="space-y-1">
              <p className="text-[#AAAAAA]">Namespace</p>
              <p>fr.neamar.kiss</p>
            </div>
            <div className="space-y-1">
              <p className="text-[#AAAAAA]">Target SDK</p>
              <p>36</p>
            </div>
            <div className="space-y-1">
              <p className="text-[#AAAAAA]">Language</p>
              <p>Java 11</p>
            </div>
          </div>

          <div className="pt-4 space-y-2">
            <h3 className="text-sm font-semibold text-[#AAAAAA] uppercase tracking-widest">Active Implementation</h3>
            <ul className="space-y-1 text-sm">
              <li className="flex items-center space-x-2">
                <span className="w-1.5 h-1.5 bg-[#4CAF50] rounded-full"></span>
                <span>Project Structure & Build Files</span>
              </li>
              <li className="flex items-center space-x-2">
                <span className="w-1.5 h-1.5 bg-[#4CAF50] rounded-full"></span>
                <span>GitHub Actions CI/CD (.vianbackup ready)</span>
              </li>
              <li className="flex items-center space-x-2">
                <span className="w-1.5 h-1.5 bg-[#FFC107] animate-pulse rounded-full"></span>
                <span>Dark Theme & Base UI</span>
              </li>
            </ul>
          </div>
        </div>

        <div className="pt-8 text-xs text-[#AAAAAA] uppercase tracking-widest opacity-50">
          Native Android Project — Export to build APK
        </div>
      </div>
    </div>
  );
}
